package com.fooddelivery.ondc.fulfillment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ondc.beckn.bpp.BppCallbackService;
import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.entity.OndcTransaction;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import com.fooddelivery.common.entity.IdempotencyKey;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.Map;
import java.util.UUID;
import static com.fooddelivery.ondc.config.OndcKafkaConfig.TOPIC_ONDC_ORDER_STATUS_CHANGED;

/**
 * Listens to Kafka events from CustomerApplication and DeliveryExecutiveApplication
 * and proactively pushes on_status callbacks to BAPs.
 * 
 * This enables proactive status updates (not just on BAP polling).
 */
@Service
@lombok.extern.slf4j.Slf4j
public class ProactiveStatusPublisher {

    private final FulfillmentStateMachine stateMachine;
    private final OndcFulfillmentMapper fulfillmentMapper;
    private final ObjectMapper objectMapper;
    private final OndcTransactionRepository transactionRepository;
    private final BppCallbackService callbackService;
    private final IIdempotencyKeyRepository idempotencyKeyRepository;

    public ProactiveStatusPublisher(FulfillmentStateMachine stateMachine, OndcFulfillmentMapper fulfillmentMapper, ObjectMapper objectMapper, OndcTransactionRepository transactionRepository, BppCallbackService callbackService, IIdempotencyKeyRepository idempotencyKeyRepository) {
        this.stateMachine = stateMachine;
        this.fulfillmentMapper = fulfillmentMapper;
        this.objectMapper = objectMapper;
        this.transactionRepository = transactionRepository;
        this.callbackService = callbackService;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = TOPIC_ONDC_ORDER_STATUS_CHANGED, groupId = "ondc-status-group")
    public void handleStatusChange(String event, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        log.info("Received order status change event: {}", event);
        try {
            java.util.Map<String, Object> eventData = objectMapper.readValue(event, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });
            String transactionId = (String) eventData.get("transactionId");
            String internalStatus = (String) eventData.get("status");
            if (transactionId == null || internalStatus == null) return;
            
            String extractedEventId = com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId");
            final String resolvedEventId;
            if (extractedEventId == null) {
                resolvedEventId = UUID.nameUUIDFromBytes(event.getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
            } else {
                resolvedEventId = extractedEventId;
            }

            String idempotencyKeyStr = "processed_event:" + resolvedEventId;

            if (idempotencyKeyRepository.existsById(idempotencyKeyStr)) {
                log.info("Duplicate order status change event ignored: {}", idempotencyKeyStr);
                return;
            }

            // Map internal status to ONDC fulfillment state
            OndcFulfillmentState newState = fulfillmentMapper.mapFromInternalStatus(internalStatus);
            stateMachine.transition(transactionId, newState);
            // Retrieve transaction to get BAP details
            OndcTransaction txn = transactionRepository.findTopByTransactionIdOrderByCreatedAtDesc(transactionId).orElseThrow(() -> new IllegalStateException("Transaction not found for id: " + transactionId));
            // Reconstruct context
            OndcContext context = new OndcContext();
            context.setTransactionId(transactionId);
            context.setBapId(txn.getBapId());
            // In full implementation, bapUri is stored in DB. We use a proxy here or fallback.
            // Assuming bapId acts as URI for simplified purposes or fetch from registry
            context.setBapUri(txn.getBapId());
            context.setBppId(txn.getBppId());
            // Send on_status
            Map<String, Object> onStatusPayload = Map.of("fulfillment", Map.of("state", Map.of("descriptor", Map.of("code", newState.getOndcValue()))));
            callbackService.sendCallbackWithRetry("on_status", context, onStatusPayload);
            log.info("Transitioned and pushed ONDC transaction {} to {}", transactionId, newState);

            try {
                if (!idempotencyKeyRepository.existsById(idempotencyKeyStr)) {
                    idempotencyKeyRepository.save(new IdempotencyKey(idempotencyKeyStr));
                }
            } catch (Exception e) {
                log.warn("Failed to save idempotency key {}, but external action was completed", idempotencyKeyStr, e);
            }
        } catch (Exception e) {
            log.error("Failed to process status change event", e);
        }
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.err.println("Message failed 5 times and sent to DLT: " + topic + " - " + message);
    }
}

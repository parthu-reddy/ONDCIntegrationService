package com.fooddelivery.ondc.fulfillment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.ondc.beckn.bpp.BppCallbackService;
import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.entity.OndcTransaction;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;
import java.util.Map;
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
    @java.lang.SuppressWarnings("all")

    private final FulfillmentStateMachine stateMachine;
    private final OndcFulfillmentMapper fulfillmentMapper;
    private final ObjectMapper objectMapper;
    private final OndcTransactionRepository transactionRepository;
    private final BppCallbackService callbackService;

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = TOPIC_ONDC_ORDER_STATUS_CHANGED, groupId = "ondc-status-group")
    public void handleStatusChange(String event) {
        log.info("Received order status change event: {}", event);
        try {
            java.util.Map<String, Object> eventData = objectMapper.readValue(event, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });
            String transactionId = (String) eventData.get("transactionId");
            String internalStatus = (String) eventData.get("status");
            if (transactionId == null || internalStatus == null) return;
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
        } catch (Exception e) {
            log.error("Failed to process status change event", e);
        }
    }

    @java.lang.SuppressWarnings("all")
    public ProactiveStatusPublisher(final FulfillmentStateMachine stateMachine, final OndcFulfillmentMapper fulfillmentMapper, final ObjectMapper objectMapper, final OndcTransactionRepository transactionRepository, final BppCallbackService callbackService) {
        this.stateMachine = stateMachine;
        this.fulfillmentMapper = fulfillmentMapper;
        this.objectMapper = objectMapper;
        this.transactionRepository = transactionRepository;
        this.callbackService = callbackService;
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.err.println("Message failed 5 times and sent to DLT: " + topic + " - " + message);
    }
}

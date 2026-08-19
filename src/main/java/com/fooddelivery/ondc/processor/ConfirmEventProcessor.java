package com.fooddelivery.ondc.processor;

import com.fooddelivery.ondc.beckn.bap.BapConfirmService;
import com.fooddelivery.ondc.config.OndcKafkaConfig;
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

/**
 * Listens for internal order creation events (from CustomerService) and
 * triggers ONDC BAP confirm.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class ConfirmEventProcessor {

    private final BapConfirmService bapConfirmService;
    private final IIdempotencyKeyRepository idempotencyKeyRepository;

    public ConfirmEventProcessor(BapConfirmService bapConfirmService, IIdempotencyKeyRepository idempotencyKeyRepository) {
        this.bapConfirmService = bapConfirmService;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = OndcKafkaConfig.TOPIC_ONDC_ORDER_CREATED, groupId = "ondc-integration-group")
    public void handleOrderCreated(String eventJson, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        log.info("Received internal order created event: {}", eventJson);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper()
                    .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map<String, Object> request = mapper.readValue(eventJson, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });

            // BppConfirmController publishes a serialized OndcRequest -- the Beckn envelope
            // {context, message} -- and OndcContext maps its fields to snake_case via @JsonProperty.
            // Reading transactionId/bppUri from the ROOT in camelCase always yielded null, so
            // BapConfirmService.confirm was never invoked and no on_confirm callback ever reached
            // the buyer app. Bind to the DTOs so the @JsonProperty mappings are applied.
            com.fooddelivery.ondc.dto.OndcRequest ondcRequest =
                    mapper.readValue(eventJson, com.fooddelivery.ondc.dto.OndcRequest.class);
            com.fooddelivery.ondc.dto.OndcContext context = ondcRequest.getContext();
            String transactionId = context != null ? context.getTransactionId() : null;
            String bppUri = context != null ? context.getBppUri() : null;
            
            String extractedEventId = com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId");
            final String resolvedEventId;
            if (extractedEventId == null) {
                resolvedEventId = UUID.nameUUIDFromBytes(eventJson.getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString();
            } else {
                resolvedEventId = extractedEventId;
            }

            String idempotencyKeyStr = "processed_event:" + resolvedEventId;

            if (idempotencyKeyRepository.existsById(idempotencyKeyStr)) {
                log.info("Duplicate order confirm event ignored: {}", idempotencyKeyStr);
                return;
            }

            // Assuming BapConfirmService needs transactionId and order payload to confirm
            if (transactionId != null && bppUri != null) {
                bapConfirmService.confirm(bppUri, transactionId, request);
            } else {
                log.warn("Invalid confirm request payload: missing transactionId or bppUri");
            }

            try {
                if (!idempotencyKeyRepository.existsById(idempotencyKeyStr)) {
                    idempotencyKeyRepository.save(new IdempotencyKey(idempotencyKeyStr));
                }
            } catch (Exception e) {
                log.warn("Failed to save idempotency key {}, but external action was completed", idempotencyKeyStr, e);
            }
        } catch (Exception e) {
            log.error("Failed to process order confirm event", e);
        }
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.err.println("Message failed 5 times and sent to DLT: " + topic + " - " + message);
    }
}

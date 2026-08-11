package com.fooddelivery.ondc.processor;

import com.fooddelivery.ondc.beckn.bap.BapConfirmService;
import com.fooddelivery.ondc.config.OndcKafkaConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Listens for internal order creation events (from CustomerService) and
 * triggers ONDC BAP confirm.
 */
@Service
public class ConfirmEventProcessor {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ConfirmEventProcessor.class);
    private final BapConfirmService bapConfirmService;

    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 1000, multiplier = 2.0), autoCreateTopics = "true", dltStrategy = DltStrategy.FAIL_ON_ERROR)
    @KafkaListener(topics = OndcKafkaConfig.TOPIC_ONDC_ORDER_CREATED, groupId = "ondc-integration-group")
    public void handleOrderCreated(String eventJson) {
        log.info("Received internal order created event: {}", eventJson);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> request = mapper.readValue(eventJson, new com.fasterxml.jackson.core.type.TypeReference<>() {
            });
            String transactionId = (String) request.get("transactionId");
            String bppUri = (String) request.get("bppUri");
            // Assuming BapConfirmService needs transactionId and order payload to confirm
            if (transactionId != null && bppUri != null) {
                bapConfirmService.confirm(bppUri, transactionId, request);
            } else {
                log.warn("Invalid confirm request payload: missing transactionId or bppUri");
            }
        } catch (Exception e) {
            log.error("Failed to process order confirm event", e);
        }
    }

    @java.lang.SuppressWarnings("all")
    public ConfirmEventProcessor(final BapConfirmService bapConfirmService) {
        this.bapConfirmService = bapConfirmService;
    }

    @DltHandler
    public void handleDlt(Object message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.err.println("Message failed 5 times and sent to DLT: " + topic + " - " + message);
    }
}

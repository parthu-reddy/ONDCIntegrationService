package com.fooddelivery.ondc.processor;

import com.fooddelivery.ondc.beckn.bap.BapConfirmService;
import com.fooddelivery.ondc.config.OndcKafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Listens for internal order creation events (from CustomerService) and
 * triggers ONDC BAP confirm.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ConfirmEventProcessor {

    private final BapConfirmService bapConfirmService;

    @KafkaListener(topics = OndcKafkaConfig.TOPIC_ONDC_ORDER_CREATED, groupId = "ondc-integration-group")
    public void handleOrderCreated(String eventJson) {
        log.info("Received internal order created event: {}", eventJson);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> request = mapper.readValue(eventJson, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            
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
}

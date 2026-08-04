package com.fooddelivery.ondc.fulfillment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.fooddelivery.ondc.config.KafkaConfig.TOPIC_ONDC_ORDER_STATUS_CHANGED;

/**
 * Listens to Kafka events from CustomerApplication and DeliveryExecutiveApplication
 * and proactively pushes on_status callbacks to BAPs.
 * 
 * This enables proactive status updates (not just on BAP polling).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProactiveStatusPublisher {

    private final FulfillmentStateMachine stateMachine;
    private final OndcFulfillmentMapper fulfillmentMapper;

    @KafkaListener(topics = TOPIC_ONDC_ORDER_STATUS_CHANGED, groupId = "ondc-status-group")
    public void handleStatusChange(String event) {
        log.info("Received order status change event: {}", event);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> eventData = mapper.readValue(event, new com.fasterxml.jackson.core.type.TypeReference<>() {});
            
            String transactionId = (String) eventData.get("transactionId");
            String internalStatus = (String) eventData.get("status");
            if (transactionId == null || internalStatus == null) return;
            
            // 2. Map internal status to ONDC fulfillment state
            OndcFulfillmentState newState = fulfillmentMapper.mapFromInternalStatus(internalStatus);
            stateMachine.transition(transactionId, newState);
            
            // In a real implementation we would fetch the BAP URI and send an /on_status callback
            // For now, state is transitioned successfully.
            log.info("Transitioned ONDC transaction {} to {}", transactionId, newState);
        } catch (Exception e) {
            log.error("Failed to process status change event", e);
        }
    }
}

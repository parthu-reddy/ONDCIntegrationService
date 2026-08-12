package com.fooddelivery.ondc.client;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component("ondcDeliveryServiceClientFallback")
public class DeliveryServiceClientFallback implements DeliveryServiceClient {
    @Override
    public Map<String, Object> assignDeliveryFromOndc(Map<String, Object> deliveryPayload) {
        throw new IllegalStateException("Delivery service is currently unavailable.");
    }

    @Override
    public Map<String, Object> getDeliveryStatus(Long orderId) {
        throw new IllegalStateException("Delivery service is currently unavailable.");
    }

    @Override
    public Map<String, Object> getDeliveryTracking(Long orderId) {
        throw new IllegalStateException("Delivery service is currently unavailable.");
    }
}

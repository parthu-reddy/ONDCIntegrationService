package com.fooddelivery.ondc.client;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class CustomerServiceClientFallback implements CustomerServiceClient {
    @Override
    public Map<String, Object> createOrderFromOndc(Map<String, Object> orderPayload) {
        throw new IllegalStateException("Customer service is currently unavailable. Failing fast to ensure financial integrity.");
    }

    @Override
    public Map<String, Object> getOrderById(Long orderId) {
        throw new IllegalStateException("Customer service is currently unavailable. Failing fast to ensure financial integrity.");
    }

    @Override
    public Map<String, Object> getOrderByTransactionId(String transactionId) {
        throw new IllegalStateException("Customer service is currently unavailable. Failing fast to ensure financial integrity.");
    }
}

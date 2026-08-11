package com.fooddelivery.ondc.client;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class PaymentServiceClientFallback implements PaymentServiceClient {
    @Override
    public Map<String, Object> getPaymentStatus(Long orderId) {
        throw new IllegalStateException("Payment service is currently unavailable. Failing fast to ensure financial integrity.");
    }
}

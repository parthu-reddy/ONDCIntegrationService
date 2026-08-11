package com.fooddelivery.ondc.client;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class IdentityServiceClientFallback implements IdentityServiceClient {
    @Override
    public Map<String, Object> getUserById(String userId) {
        throw new IllegalStateException("Identity service is currently unavailable. Failing fast to ensure financial integrity.");
    }
}

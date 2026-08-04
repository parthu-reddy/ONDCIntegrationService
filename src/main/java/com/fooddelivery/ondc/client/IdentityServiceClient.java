package com.fooddelivery.ondc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign client for IdentityService (Eureka: identity-service).
 * Used for user identity resolution during ONDC order processing.
 */
@FeignClient(name = "identity-service")
public interface IdentityServiceClient {

    @GetMapping("/api/v1/internal/users/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") String userId);
}

package com.fooddelivery.ondc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client for PaymentGatewayIntegration (Eureka: payment-service).
 * Used for processing ONDC settlement payments and refunds.
 * 
 * CRITICAL: No default values — if payment processing fails,
 * the caller MUST throw IllegalStateException per financial integrity rules.
 */
@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @GetMapping("/api/v1/payments/status")
    Map<String, Object> getPaymentStatus(@RequestParam("orderId") Long orderId);
}

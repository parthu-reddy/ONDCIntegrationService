package com.fooddelivery.ondc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign client for CustomerApplication (Eureka: customer-service).
 * Used to create orders from ONDC /confirm and query order status.
 */
@FeignClient(name = "customer-application", fallback = CustomerServiceClientFallback.class)
public interface CustomerServiceClient {

    @PostMapping("/api/v1/orders/create-from-ondc")
    Map<String, Object> createOrderFromOndc(@RequestBody Map<String, Object> orderPayload);

    @GetMapping("/api/v1/orders/{orderId}")
    Map<String, Object> getOrderById(@PathVariable("orderId") Long orderId);

    @GetMapping("/api/v1/orders/by-transaction/{transactionId}")
    Map<String, Object> getOrderByTransactionId(@PathVariable("transactionId") String transactionId);
}

package com.fooddelivery.ondc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Feign client for DeliveryExecutiveApplication (Eureka: delivery-service).
 * Used to assign delivery, fetch driver location, and query delivery status.
 */
@FeignClient(name = "delivery-executive-application", fallback = DeliveryServiceClientFallback.class)
public interface DeliveryServiceClient {

    @PostMapping("/api/v1/delivery/assign-from-ondc")
    Map<String, Object> assignDeliveryFromOndc(@RequestBody Map<String, Object> deliveryPayload);

    @GetMapping("/api/v1/delivery/orders/{orderId}/status")
    Map<String, Object> getDeliveryStatus(@PathVariable("orderId") Long orderId);

    @GetMapping("/api/v1/delivery/orders/{orderId}/tracking")
    Map<String, Object> getDeliveryTracking(@PathVariable("orderId") Long orderId);
}

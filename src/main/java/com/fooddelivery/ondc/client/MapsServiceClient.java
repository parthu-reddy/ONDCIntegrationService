package com.fooddelivery.ondc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client for MapsIntegration (Eureka: mapsintegration).
 * Used to compute delivery distances for ONDC quote calculations.
 *
 * CRITICAL: If distance call fails, throw IllegalArgumentException.
 * No default distance values (per financial integrity rules).
 */
@FeignClient(name = "mapsintegration")
public interface MapsServiceClient {

    @GetMapping("/api/logistics/distance")
    Map<String, Object> getDistance(
            @RequestParam("originLat") double originLat,
            @RequestParam("originLng") double originLng,
            @RequestParam("destLat") double destLat,
            @RequestParam("destLng") double destLng);
}

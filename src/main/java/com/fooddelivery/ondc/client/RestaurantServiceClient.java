package com.fooddelivery.ondc.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Feign client for RestaurantApplication (Eureka: restaurant-service).
 * Used to fetch outlet info, menus, and catalogs for ONDC syndication.
 */
@FeignClient(name = "restaurant-service", fallback = RestaurantServiceClientFallback.class)
public interface RestaurantServiceClient {

    @GetMapping("/api/v1/restaurants/nearby")
    Map<String, Object> getServiceableRestaurants(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam("radiusKm") double radiusKm);

    @GetMapping("/api/v1/outlets/{outletId}")
    Map<String, Object> getOutletById(@PathVariable("outletId") Long outletId);

    @GetMapping("/api/v1/outlets/{outletId}/categories")
    List<Map<String, Object>> getOutletMenu(@PathVariable("outletId") Long outletId);

    @GetMapping("/api/v1/brands/{brandId}")
    Map<String, Object> getBrandById(@PathVariable("brandId") Long brandId);
}

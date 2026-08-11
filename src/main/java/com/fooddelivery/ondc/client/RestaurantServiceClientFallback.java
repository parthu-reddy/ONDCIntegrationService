package com.fooddelivery.ondc.client;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class RestaurantServiceClientFallback implements RestaurantServiceClient {
    @Override
    public Map<String, Object> getServiceableRestaurants(double latitude, double longitude, double radiusKm) {
        throw new IllegalStateException("Restaurant service is currently unavailable.");
    }

    @Override
    public Map<String, Object> getOutletById(Long outletId) {
        throw new IllegalStateException("Restaurant service is currently unavailable.");
    }

    @Override
    public List<Map<String, Object>> getOutletMenu(Long outletId) {
        throw new IllegalStateException("Restaurant service is currently unavailable.");
    }

    @Override
    public Map<String, Object> getBrandById(Long brandId) {
        throw new IllegalStateException("Restaurant service is currently unavailable.");
    }
}

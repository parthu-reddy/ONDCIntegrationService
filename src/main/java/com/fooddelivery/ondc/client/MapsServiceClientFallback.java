package com.fooddelivery.ondc.client;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class MapsServiceClientFallback implements MapsServiceClient {
    @Override
    public Map<String, Object> getDistance(double originLat, double originLng, double destLat, double destLng) {
        throw new IllegalArgumentException("Maps service is currently unavailable. Failing fast to ensure financial integrity.");
    }
}

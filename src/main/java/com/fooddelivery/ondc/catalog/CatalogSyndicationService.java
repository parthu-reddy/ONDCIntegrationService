package com.fooddelivery.ondc.catalog;

import com.fooddelivery.ondc.client.RestaurantServiceClient;
import com.fooddelivery.ondc.config.OndcProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Core catalog syndication service — fetches menu data from RestaurantApplication
 * via Feign and transforms it into ONDC-compliant catalog format.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogSyndicationService {

    private final RestaurantServiceClient restaurantServiceClient;
    private final OndcCatalogMapper catalogMapper;
    private final CatalogValidationService validationService;
    private final TaxonomyMappingService taxonomyMapper;
    private final OperatingHoursMapper operatingHoursMapper;
    private final OndcProperties ondcProperties;

    /**
     * Builds a full ONDC catalog for all serviceable outlets in a given city/GPS area.
     * Used in response to /search requests.
     *
     * @param city ONDC city code
     * @param gps  GPS coordinates "lat,lng"
     * @return ONDC catalog payload (as Map for flexibility)
     */
    public Object buildCatalogForSearch(String city, String gps) {
        log.info("Building ONDC catalog for city: {}, gps: {}", city, gps);

        // 1. Fetch serviceable outlets from RestaurantApplication (mocked or actual Feign call)
        java.util.List<java.util.Map<String, Object>> outlets = fetchServiceableOutlets(city, gps);

        if (outlets == null || outlets.isEmpty()) {
            throw new IllegalStateException("No serviceable outlets found for location: " + gps);
        }

        // 2. Map outlets and items to ONDC Providers
        java.util.List<java.util.Map<String, Object>> ondcProviders = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> outlet : outlets) {
            String fssai = (String) outlet.get("fssai");
            try {
                validationService.validateFssai(fssai);
            } catch (Exception e) {
                log.warn("Skipping outlet {} due to invalid FSSAI: {}", outlet.get("id"), fssai);
                continue;
            }

            java.util.Map<String, Object> provider = new java.util.HashMap<>();
            provider.put("id", outlet.get("id"));
            
            // Map details via catalog mapper
            provider.put("descriptor", java.util.Map.of(
                "name", outlet.get("name"),
                "short_desc", outlet.get("description"),
                "long_desc", outlet.get("description"),
                "images", java.util.List.of(outlet.get("imageUrl"))
            ));

            provider.put("categories", java.util.List.of(
                java.util.Map.of("id", taxonomyMapper.mapInternalCategoryToOndcCode((String) outlet.get("category")), "descriptor", java.util.Map.of("name", outlet.get("category")))
            ));
            
            // Map Operating Hours
            String hours = (String) outlet.get("operatingHours");
            if (hours != null) {
                provider.put("time", operatingHoursMapper.mapToOndcTimeRange(hours));
            }

            // Fetch and map items
            try {
                Long outletId = Long.valueOf(outlet.get("id").toString());
                java.util.List<java.util.Map<String, Object>> menuItems = restaurantServiceClient.getOutletMenu(outletId);
                java.util.List<java.util.Map<String, Object>> ondcItems = new java.util.ArrayList<>();
                if (menuItems != null) {
                    for (java.util.Map<String, Object> item : menuItems) {
                        ondcItems.add(catalogMapper.mapMenuItemToItem(item));
                    }
                }
                provider.put("items", ondcItems);
            } catch (Exception e) {
                log.error("Failed to fetch menu items for outlet {}", outlet.get("id"), e);
                provider.put("items", java.util.List.of());
            }
            
            ondcProviders.add(provider);
        }

        // Return ONDC bpp/providers catalog
        return java.util.Map.of("bpp/providers", ondcProviders);
    }

    private java.util.List<java.util.Map<String, Object>> fetchServiceableOutlets(String city, String gps) {
        try {
            // Using Feign client to get list of restaurants
            if (gps != null && gps.contains(",")) {
                String[] coords = gps.split(",");
                double lat = Double.parseDouble(coords[0].trim());
                double lon = Double.parseDouble(coords[1].trim());
                double radius = ondcProperties.getFulfillment().getMaxDeliveryRadiusKm();
                
                java.util.Map<String, Object> response = restaurantServiceClient.getServiceableRestaurants(lat, lon, radius);
                if (response != null && response.containsKey("restaurants")) {
                    return (java.util.List<java.util.Map<String, Object>>) response.get("restaurants");
                }
            }
            return java.util.List.of();
        } catch (Exception e) {
            log.error("Failed to fetch outlets from RestaurantApplication", e);
            throw new IllegalStateException("Failed to retrieve serviceable outlets from RestaurantService", e);
        }
    }
}

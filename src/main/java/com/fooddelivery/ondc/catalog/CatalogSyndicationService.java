package com.fooddelivery.ondc.catalog;

import com.fooddelivery.ondc.client.RestaurantServiceClient;
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

        // TODO: 1. Fetch serviceable outlets from RestaurantApplication (PostGIS filter)
        // TODO: 2. For each outlet, fetch menu items, operating hours, FSSAI
        // TODO: 3. Map to ONDC catalog schema via OndcCatalogMapper
        // TODO: 4. Apply taxonomy mapping for categories
        // TODO: 5. Validate FSSAI (14 digits), GSTIN format
        // TODO: 6. Return ONDC bpp/providers catalog

        return null; // Placeholder — will return ONDC catalog object
    }
}

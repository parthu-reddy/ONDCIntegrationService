package com.fooddelivery.ondc.catalog;

import org.springframework.stereotype.Service;

/**
 * Maps internal cuisine categories to ONDC:RET11 taxonomy codes.
 */
@Service
@lombok.extern.slf4j.Slf4j
public class TaxonomyMappingService {
    @java.lang.SuppressWarnings("all")

    /**
     * Maps an internal cuisine or category string to the ONDC:RET11 taxonomy code.
     */
    public String mapInternalCategoryToOndcCode(String internalCategory) {
        if (internalCategory == null) {
            throw new IllegalArgumentException("Internal category cannot be null");
        }
        return switch (internalCategory.toUpperCase()) {
            case "GROCERY" -> "Grocery";
            case "FRUITS", "VEGETABLES" -> "F&V";
            case "PACKAGED FOOD" -> "Packaged Foods";
            case "BEVERAGE", "BEVERAGES" -> "Beverages";
            case "BAKERY", "CAKE", "DESSERT" -> "Bakes & Desserts";
            case "MEAT", "POULTRY", "SEAFOOD" -> "Meat & Seafood";
            case "STREET FOOD", "CHAAT" -> "Street Food";
            default -> "F&B";
            // Fallback to generic Food & Beverage
        };
    }
}

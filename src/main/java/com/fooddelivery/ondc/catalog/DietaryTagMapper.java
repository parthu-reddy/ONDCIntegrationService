package com.fooddelivery.ondc.catalog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Maps internal veg/non-veg/egg markers to ONDC @ondc/org/veg_nonveg tags. */
@Component @Slf4j
public class DietaryTagMapper {

    public String mapToOndcTag(String internalTag) {
        if (internalTag == null) {
            throw new IllegalArgumentException("Dietary tag cannot be null");
        }
        return switch (internalTag.toUpperCase()) {
            case "VEG", "VEGETARIAN" -> "veg";
            case "NON_VEG", "NONVEG", "NON-VEG" -> "non-veg";
            case "EGG" -> "egg";
            default -> throw new IllegalArgumentException("Unknown dietary tag: " + internalTag);
        };
    }
}

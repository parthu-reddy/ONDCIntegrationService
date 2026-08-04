package com.fooddelivery.ondc.catalog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Maps outlet operating hours to ONDC time descriptor format (ISO 8601). */
@Component @Slf4j
public class OperatingHoursMapper {
    /**
     * Converts a string like "09:00-22:00" to ONDC time.range format.
     */
    public java.util.Map<String, Object> mapToOndcTimeRange(String operatingHours) {
        if (operatingHours == null || !operatingHours.contains("-")) {
            return java.util.Map.of();
        }
        String[] parts = operatingHours.split("-");
        if (parts.length != 2) return java.util.Map.of();

        return java.util.Map.of(
            "range", java.util.Map.of(
                "start", parts[0].trim().length() == 5 ? parts[0].trim() + ":00" : parts[0].trim(),
                "end", parts[1].trim().length() == 5 ? parts[1].trim() + ":00" : parts[1].trim()
            )
        );
    }
}

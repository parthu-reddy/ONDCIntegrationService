package com.fooddelivery.ondc.catalog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Maps internal menu/outlet entities to ONDC catalog schema.
 * Brand → Provider, Outlet → Location, MenuItem → Item.
 */
@Component
@Slf4j
public class OndcCatalogMapper {

    /**
     * Maps an internal brand map to an ONDC Provider.
     */
    public Map<String, Object> mapBrandToProvider(Map<String, Object> brand) {
        if (brand == null) return null;
        return Map.of(
                "id", String.valueOf(brand.get("id")),
                "descriptor", Map.of(
                        "name", brand.get("name"),
                        "short_desc", brand.getOrDefault("description", ""),
                        "long_desc", brand.getOrDefault("description", ""),
                        "images", List.of(brand.getOrDefault("logoUrl", ""))
                ),
                "time", Map.of(
                        "label", "enable",
                        "timestamp", Instant.now().toString()
                )
        );
    }

    /**
     * Maps an internal outlet map to an ONDC Location.
     */
    public Map<String, Object> mapOutletToLocation(Map<String, Object> outlet) {
        if (outlet == null) return null;
        return Map.of(
                "id", String.valueOf(outlet.get("id")),
                "gps", outlet.get("latitude") + "," + outlet.get("longitude"),
                "address", Map.of(
                        "street", outlet.getOrDefault("street", ""),
                        "city", outlet.getOrDefault("city", ""),
                        "state", outlet.getOrDefault("state", ""),
                        "area_code", outlet.getOrDefault("pincode", "")
                )
        );
    }

    /**
     * Maps an internal menu item map to an ONDC Item.
     */
    public Map<String, Object> mapMenuItemToItem(Map<String, Object> item) {
        if (item == null) return null;
        return Map.of(
                "id", String.valueOf(item.get("id")),
                "descriptor", Map.of(
                        "name", item.get("name"),
                        "short_desc", item.getOrDefault("description", ""),
                        "images", List.of(item.getOrDefault("imageUrl", ""))
                ),
                "price", Map.of(
                        "currency", "INR",
                        "value", String.valueOf(item.get("price"))
                ),
                "location_id", String.valueOf(item.get("outletId")),
                "category_id", String.valueOf(item.get("categoryId"))
        );
    }
}

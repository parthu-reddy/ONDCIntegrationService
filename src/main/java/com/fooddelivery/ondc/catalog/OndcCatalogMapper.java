package com.fooddelivery.ondc.catalog;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Maps internal menu/outlet entities to ONDC catalog schema.
 * Brand → Provider, Outlet → Location, MenuItem → Item.
 */
@Component
public class OndcCatalogMapper {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OndcCatalogMapper.class);
    private final DietaryTagMapper dietaryTagMapper;

    public OndcCatalogMapper(DietaryTagMapper dietaryTagMapper) {
        this.dietaryTagMapper = dietaryTagMapper;
    }

    /**
     * Maps an internal brand map to an ONDC Provider.
     */
    public Map<String, Object> mapBrandToProvider(Map<String, Object> brand) {
        if (brand == null) return null;
        return Map.of("id", String.valueOf(brand.get("id")), "descriptor", Map.of("name", brand.get("name"), "short_desc", brand.getOrDefault("description", ""), "long_desc", brand.getOrDefault("description", ""), "images", List.of(brand.getOrDefault("logoUrl", ""))), "time", Map.of("label", "enable", "timestamp", Instant.now().toString()));
    }

    /**
     * Maps an internal outlet map to an ONDC Location.
     */
    public Map<String, Object> mapOutletToLocation(Map<String, Object> outlet) {
        if (outlet == null) return null;
        return Map.of("id", String.valueOf(outlet.get("id")), "gps", outlet.get("latitude") + "," + outlet.get("longitude"), "address", Map.of("street", outlet.getOrDefault("street", ""), "city", outlet.getOrDefault("city", ""), "state", outlet.getOrDefault("state", ""), "area_code", outlet.getOrDefault("pincode", "")));
    }

    /**
     * Maps an internal menu item map to an ONDC Item.
     */
    public Map<String, Object> mapMenuItemToItem(Map<String, Object> item) {
        if (item == null) return null;
        Map<String, Object> mappedItem = new HashMap<>();
        mappedItem.put("id", String.valueOf(item.get("id")));
        mappedItem.put("descriptor", Map.of("name", item.get("name"), "short_desc", item.getOrDefault("description", ""), "images", List.of(item.getOrDefault("imageUrl", ""))));
        mappedItem.put("price", Map.of("currency", "INR", "value", String.valueOf(item.get("price"))));
        mappedItem.put("location_id", String.valueOf(item.get("outletId")));
        mappedItem.put("category_id", String.valueOf(item.get("categoryId")));
        if (item.containsKey("dietaryTag") && item.get("dietaryTag") != null) {
            String ondcTag = dietaryTagMapper.mapToOndcTag((String) item.get("dietaryTag"));
            mappedItem.put("tags", List.of(Map.of("code", "type", "list", List.of(Map.of("code", "type", "value", "item"))), Map.of("code", "@ondc/org/veg_nonveg", "list", List.of(Map.of("code", ondcTag, "value", "yes")))));
        }
        return mappedItem;
    }
}

package com.fooddelivery.ondc.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class OndcCatalogMapperTest {

    private OndcCatalogMapper ondcCatalogMapper;
    private DietaryTagMapper dietaryTagMapper;

    @BeforeEach
    void setUp() {
        dietaryTagMapper = Mockito.mock(DietaryTagMapper.class);
        ondcCatalogMapper = new OndcCatalogMapper(dietaryTagMapper);
    }

    @Test
    void testMapMenuItemToItem_withDietaryTag() {
        // Arrange
        when(dietaryTagMapper.mapToOndcTag("VEG")).thenReturn("veg");

        Map<String, Object> item = Map.of(
                "id", "item-123",
                "name", "Paneer Tikka",
                "price", 150.50,
                "outletId", "outlet-456",
                "categoryId", "cat-789",
                "dietaryTag", "VEG"
        );

        // Act
        Map<String, Object> mappedItem = ondcCatalogMapper.mapMenuItemToItem(item);

        // Assert
        assertNotNull(mappedItem);
        assertEquals("item-123", mappedItem.get("id"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tags = (List<Map<String, Object>>) mappedItem.get("tags");
        assertNotNull(tags);
        assertEquals(2, tags.size());
        
        // Ensure @ondc/org/veg_nonveg tag is properly set
        Map<String, Object> vegNonVegTag = tags.stream()
                .filter(t -> "@ondc/org/veg_nonveg".equals(t.get("code")))
                .findFirst()
                .orElse(null);
                
        assertNotNull(vegNonVegTag);
    }

    @Test
    void testMapMenuItemToItem_withoutDietaryTag() {
        // Arrange
        Map<String, Object> item = Map.of(
                "id", "item-123",
                "name", "Water Bottle",
                "price", 20.0,
                "outletId", "outlet-456",
                "categoryId", "cat-789"
        );

        // Act
        Map<String, Object> mappedItem = ondcCatalogMapper.mapMenuItemToItem(item);

        // Assert
        assertNotNull(mappedItem);
        assertFalse(mappedItem.containsKey("tags"));
    }
}

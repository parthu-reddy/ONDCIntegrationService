package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Typed representation of the Beckn Catalog object returned in /on_search responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OndcCatalog {
    private OndcCatalogDescriptor descriptor;
    private java.util.List<OndcCatalogProvider> providers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcCatalogDescriptor {
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcCatalogProvider {
        private String id;
        private OndcCatalogDescriptor descriptor;
        private java.util.List<OndcCatalogItem> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcCatalogItem {
        private String id;
        private OndcCatalogDescriptor descriptor;
        private OndcCatalogPrice price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcCatalogPrice {
        private String currency;
        private String value;
    }
}

package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * Typed representation of the Beckn Order object used in /select, /init, /confirm, /cancel, /update.
 * Models the subset of the ONDC order schema that our platform uses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OndcOrder {
    private String id;
    private String state;
    private OndcProvider provider;
    private List<OndcOrderItem> items;
    private OndcPaymentInfo payment;
    private OndcCancellation cancellation;
    private OndcOrderFulfillment fulfillment;
    private Object rating;
    private List<Map<String, Object>> orders; // Used in RSF /recon API

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcProvider {
        private String id;
        private String descriptor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcOrderItem {
        private String id;
        private int quantity;
        private OndcPrice price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcPrice {
        private String currency;
        private String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcPaymentInfo {
        private String type;
        private String status;
        @JsonProperty("@ondc/org/settlement_details")
        private Map<String, String> settlementDetails;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcCancellation {
        @JsonProperty("cancelled_by")
        private String cancelledBy;
        private OndcCancellationReason reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcCancellationReason {
        private String id;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcOrderFulfillment {
        private OndcFulfillmentState state;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcFulfillmentState {
        private OndcStateDescriptor descriptor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OndcStateDescriptor {
        private String code;
    }
}

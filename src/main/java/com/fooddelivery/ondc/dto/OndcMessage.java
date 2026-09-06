package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Strongly-typed ONDC Beckn message wrapper.
 * Each field corresponds to a specific Beckn action's payload.
 * Only the relevant field is populated for a given action type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OndcMessage {
    private OndcIntent intent;
    private OndcOrder order;
    private OndcCatalog catalog;
    private OndcTracking tracking;
    private OndcPayment payment;
    private OndcRating rating;
    @JsonProperty("order_id")
    private String orderId;
    private OndcSettlement settlement;
}

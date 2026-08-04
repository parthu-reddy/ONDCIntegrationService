package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic ONDC Beckn message wrapper.
 * The 'intent' or 'order' payload varies by action type.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcMessage {

    private Object intent;
    private Object order;
    private Object catalog;
    private Object tracking;
    private Object payment;
    private Object rating;
}

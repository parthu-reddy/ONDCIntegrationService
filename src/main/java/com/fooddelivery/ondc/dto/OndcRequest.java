package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard ONDC request/response envelope.
 * Every Beckn API call contains a context and message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcRequest {

    private OndcContext context;
    private OndcMessage message;

    @JsonProperty("error")
    private OndcError error;
}

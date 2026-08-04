package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ONDC standard error object returned in NACK responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcError {

    @JsonProperty("type")
    private String type;

    @JsonProperty("code")
    private String code;

    @JsonProperty("path")
    private String path;

    @JsonProperty("message")
    private String message;
}

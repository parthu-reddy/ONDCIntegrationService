package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard ONDC Beckn context object included in every request/response.
 * Maps to the Beckn Protocol context schema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcContext {

    private String domain;
    private String action;
    private String country;
    private String city;

    @JsonProperty("core_version")
    private String coreVersion;

    @JsonProperty("bap_id")
    private String bapId;

    @JsonProperty("bap_uri")
    private String bapUri;

    @JsonProperty("bpp_id")
    private String bppId;

    @JsonProperty("bpp_uri")
    private String bppUri;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("message_id")
    private String messageId;

    private String timestamp;

    private String ttl;

    @JsonProperty("key")
    private String key;
}

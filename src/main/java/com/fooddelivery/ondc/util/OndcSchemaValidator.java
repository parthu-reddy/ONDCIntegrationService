package com.fooddelivery.ondc.util;

import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.dto.OndcRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validates incoming ONDC request payloads against mandatory schema fields.
 */
@Component
@Slf4j
public class OndcSchemaValidator {

    /**
     * Validates that the request contains all mandatory context fields.
     *
     * @param request the incoming ONDC request
     * @throws IllegalArgumentException if mandatory fields are missing
     */
    public void validateRequest(OndcRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("ONDC request payload is null");
        }
        if (request.getContext() == null) {
            throw new IllegalArgumentException("ONDC context is missing");
        }

        OndcContext ctx = request.getContext();
        requireNonBlank(ctx.getDomain(), "context.domain");
        requireNonBlank(ctx.getAction(), "context.action");
        requireNonBlank(ctx.getTransactionId(), "context.transaction_id");
        requireNonBlank(ctx.getMessageId(), "context.message_id");
        requireNonBlank(ctx.getTimestamp(), "context.timestamp");
    }

    /**
     * Validates context fields specific to search requests (GPS, city).
     */
    public void validateSearchContext(OndcContext ctx) {
        requireNonBlank(ctx.getCity(), "context.city");
    }

    /**
     * Validates that order-related context fields are present.
     */
    public void validateOrderContext(OndcContext ctx) {
        requireNonBlank(ctx.getBapId(), "context.bap_id");
        requireNonBlank(ctx.getBapUri(), "context.bap_uri");
        requireNonBlank(ctx.getBppId(), "context.bpp_id");
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Mandatory ONDC field missing: " + fieldName);
        }
    }
}

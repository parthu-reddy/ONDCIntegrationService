package com.fooddelivery.ondc.util;

import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.dto.OndcRequest;
import org.springframework.stereotype.Component;

/**
 * Validates incoming ONDC request payloads against mandatory schema fields.
 */
@Component
@lombok.extern.slf4j.Slf4j
public class OndcSchemaValidator {
    @java.lang.SuppressWarnings("all")

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

    /** The one Beckn payment type this seller accepts: paid when the order is placed. */
    public static final String PREPAID = "ON-ORDER";

    /**
     * Refuses any order that is not paid up front: this platform takes prepaid orders only. Beckn says
     * when payment happens through the payment type, so without this a buyer app could confirm an
     * order to be paid on delivery and it would be published for creation like any other. on_init
     * only ever offers {@link #PREPAID}; a buyer confirming anything else is refused.
     *
     * @param paymentRequired true at /confirm, where the order is created; at /init the payment terms
     *                        are still ours to propose, so an absent payment is allowed but a
     *                        non-prepaid one is not
     */
    public void validatePrepaid(OndcRequest request, boolean paymentRequired) {
        com.fooddelivery.ondc.dto.OndcOrder order = request.getMessage() == null ? null : request.getMessage().getOrder();
        com.fooddelivery.ondc.dto.OndcOrder.OndcPaymentInfo payment = order == null ? null : order.getPayment();
        String type = payment == null ? null : payment.getType();
        if (type == null) {
            if (paymentRequired) {
                throw new IllegalArgumentException("Mandatory ONDC field missing: message.order.payment.type");
            }
            return;
        }
        if (!PREPAID.equals(type)) {
            throw new IllegalArgumentException("Payment type " + type + " is not accepted: orders must be paid when placed ("
                    + PREPAID + ")");
        }
    }

    private void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Mandatory ONDC field missing: " + fieldName);
        }
    }
}

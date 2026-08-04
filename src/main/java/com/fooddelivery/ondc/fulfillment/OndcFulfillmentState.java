package com.fooddelivery.ondc.fulfillment;

/**
 * ONDC:RET11 fulfillment states for F&B hyperlocal delivery.
 * 
 * REQUIRED states: PENDING, PACKED, ORDER_PICKED_UP, ORDER_DELIVERED
 * OPTIONAL states: AGENT_ASSIGNED, OUT_FOR_PICKUP, AT_PICKUP, AT_DELIVERY
 * FORBIDDEN states (never emitted): IN_TRANSIT, AT_DESTINATION_HUB, OUT_FOR_DELIVERY
 */
public enum OndcFulfillmentState {

    // Required states (must appear in this order)
    PENDING("Pending"),
    PACKED("Packed"),
    ORDER_PICKED_UP("Order-picked-up"),
    ORDER_DELIVERED("Order-delivered"),

    // Optional states (finer granularity)
    AGENT_ASSIGNED("Agent-assigned"),
    OUT_FOR_PICKUP("Out-for-pickup"),
    AT_PICKUP("At-pickup"),
    AT_DELIVERY("At-delivery");

    private final String ondcValue;

    OndcFulfillmentState(String ondcValue) {
        this.ondcValue = ondcValue;
    }

    /**
     * Returns the exact ONDC-compliant string value for this state.
     */
    public String getOndcValue() {
        return ondcValue;
    }

    /**
     * Parses an ONDC state string into the enum. Throws IllegalArgumentException
     * for forbidden states.
     */
    public static OndcFulfillmentState fromOndcValue(String value) {
        // Guard against forbidden states
        if ("In-transit".equalsIgnoreCase(value) ||
                "At-destination-hub".equalsIgnoreCase(value) ||
                "Out-for-delivery".equalsIgnoreCase(value)) {
            throw new IllegalStateException(
                    "Forbidden ONDC:RET11 fulfillment state: '" + value +
                            "'. Hyperlocal F&B domain does not permit retail logistics states.");
        }

        for (OndcFulfillmentState state : values()) {
            if (state.ondcValue.equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown ONDC fulfillment state: " + value);
    }
}

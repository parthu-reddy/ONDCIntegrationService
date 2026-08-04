package com.fooddelivery.ondc.exception;

public class ForbiddenFulfillmentStateException extends IllegalStateException {
    public ForbiddenFulfillmentStateException(String state) {
        super("Forbidden ONDC:RET11 fulfillment state: '" + state +
                "'. Hyperlocal F&B domain does not permit retail logistics states " +
                "(In-transit, At-destination-hub, Out-for-delivery).");
    }
}

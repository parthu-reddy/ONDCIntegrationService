package com.fooddelivery.ondc.fulfillment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Maps internal platform delivery/order statuses to ONDC fulfillment states.
 * This is the translation layer between your existing DeliveryStatus/OrderStatus
 * enums and the ONDC:RET11 fulfillment states.
 */
@Component
@Slf4j
public class OndcFulfillmentMapper {

    /**
     * Maps internal order status string to ONDC fulfillment state.
     * Extends this mapping as internal statuses evolve.
     */
    public OndcFulfillmentState mapFromInternalStatus(String internalStatus) {
        if (internalStatus == null) {
            throw new IllegalArgumentException("Internal status cannot be null");
        }

        return switch (internalStatus.toUpperCase()) {
            case "PLACED", "RECEIVED", "ACCEPTED", "PENDING" -> OndcFulfillmentState.PENDING;
            case "PREPARING", "PREPARED", "PACKED", "READY_FOR_PICKUP" -> OndcFulfillmentState.PACKED;
            case "DRIVER_ASSIGNED", "AGENT_ASSIGNED" -> OndcFulfillmentState.AGENT_ASSIGNED;
            case "DRIVER_EN_ROUTE_TO_RESTAURANT", "OUT_FOR_PICKUP" -> OndcFulfillmentState.OUT_FOR_PICKUP;
            case "DRIVER_AT_RESTAURANT", "AT_PICKUP" -> OndcFulfillmentState.AT_PICKUP;
            case "PICKED_UP", "ORDER_PICKED_UP" -> OndcFulfillmentState.ORDER_PICKED_UP;
            case "DRIVER_AT_CUSTOMER", "AT_DELIVERY" -> OndcFulfillmentState.AT_DELIVERY;
            case "DELIVERED", "ORDER_DELIVERED" -> OndcFulfillmentState.ORDER_DELIVERED;
            default -> {
                log.warn("Unknown internal status: '{}'. Defaulting to PENDING.", internalStatus);
                yield OndcFulfillmentState.PENDING;
            }
        };
    }
}

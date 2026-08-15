package com.fooddelivery.ondc.fulfillment;

import com.fooddelivery.ondc.exception.ForbiddenFulfillmentStateException;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;

/**
 * Enforces ONDC:RET11 fulfillment state transition rules.
 * Validates that transitions are one-directional and forbidden states are never emitted.
 */
@Component
@lombok.extern.slf4j.Slf4j
public class ForbiddenStateGuard {
    @java.lang.SuppressWarnings("all")

    private static final Set<String> FORBIDDEN_STATES = Set.of("In-transit", "At-destination-hub", "Out-for-delivery");
    /**
     * Valid transitions: from → set of allowed next states
     */
    private static final Map<OndcFulfillmentState, Set<OndcFulfillmentState>> VALID_TRANSITIONS = Map.of(OndcFulfillmentState.PENDING, Set.of(OndcFulfillmentState.PACKED, OndcFulfillmentState.AGENT_ASSIGNED), OndcFulfillmentState.PACKED, Set.of(OndcFulfillmentState.AGENT_ASSIGNED, OndcFulfillmentState.OUT_FOR_PICKUP, OndcFulfillmentState.ORDER_PICKED_UP), OndcFulfillmentState.AGENT_ASSIGNED, Set.of(OndcFulfillmentState.OUT_FOR_PICKUP, OndcFulfillmentState.AT_PICKUP, OndcFulfillmentState.ORDER_PICKED_UP), OndcFulfillmentState.OUT_FOR_PICKUP, Set.of(OndcFulfillmentState.AT_PICKUP, OndcFulfillmentState.ORDER_PICKED_UP), OndcFulfillmentState.AT_PICKUP, Set.of(OndcFulfillmentState.ORDER_PICKED_UP), OndcFulfillmentState.ORDER_PICKED_UP, Set.of(OndcFulfillmentState.AT_DELIVERY, OndcFulfillmentState.ORDER_DELIVERED), OndcFulfillmentState.AT_DELIVERY, Set.of(OndcFulfillmentState.ORDER_DELIVERED), OndcFulfillmentState.ORDER_DELIVERED, Set.of() // terminal state
    );

    /**
     * Validates a state transition. Throws if forbidden or invalid.
     *
     * @param from current state
     * @param to   target state
     * @throws ForbiddenFulfillmentStateException if target state is forbidden
     * @throws IllegalStateException if transition is not allowed
     */
    public void validateTransition(OndcFulfillmentState from, OndcFulfillmentState to) {
        // Guard against forbidden states
        if (FORBIDDEN_STATES.contains(to.getOndcValue())) {
            throw new ForbiddenFulfillmentStateException(to.getOndcValue());
        }
        // Validate allowed transitions
        Set<OndcFulfillmentState> allowed = VALID_TRANSITIONS.get(from);
        if (allowed == null || !allowed.contains(to)) {
            throw new IllegalStateException("Invalid fulfillment state transition: " + from.getOndcValue() + " → " + to.getOndcValue());
        }
        log.debug("Valid state transition: {} → {}", from.getOndcValue(), to.getOndcValue());
    }
}

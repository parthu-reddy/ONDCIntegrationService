package com.fooddelivery.ondc.fulfillment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages fulfillment state transitions for ONDC orders.
 * Tracks current state per transaction and validates transitions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FulfillmentStateMachine {

    private final ForbiddenStateGuard forbiddenStateGuard;

    // In-memory state tracking (production should use Redis or DB)
    private final Map<String, OndcFulfillmentState> orderStates = new ConcurrentHashMap<>();

    /**
     * Transitions an order to a new fulfillment state.
     *
     * @param transactionId ONDC transaction ID
     * @param newState      target state
     * @return the new state after transition
     */
    public OndcFulfillmentState transition(String transactionId, OndcFulfillmentState newState) {
        OndcFulfillmentState currentState = orderStates.getOrDefault(
                transactionId, OndcFulfillmentState.PENDING);

        // Skip if already at this state (idempotent)
        if (currentState == newState) {
            log.debug("Order {} already at state {}, skipping", transactionId, newState.getOndcValue());
            return currentState;
        }

        forbiddenStateGuard.validateTransition(currentState, newState);

        orderStates.put(transactionId, newState);
        log.info("Order {} transitioned: {} → {}",
                transactionId, currentState.getOndcValue(), newState.getOndcValue());

        return newState;
    }

    /**
     * Gets the current fulfillment state for an order.
     */
    public OndcFulfillmentState getCurrentState(String transactionId) {
        return orderStates.getOrDefault(transactionId, OndcFulfillmentState.PENDING);
    }

    /**
     * Removes state tracking for a completed/cancelled order.
     */
    public void removeState(String transactionId) {
        orderStates.remove(transactionId);
    }
}

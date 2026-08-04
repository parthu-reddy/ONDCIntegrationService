package com.fooddelivery.ondc.fulfillment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Manages fulfillment state transitions for ONDC orders.
 * Tracks current state per transaction in Redis and validates transitions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FulfillmentStateMachine {

    private final ForbiddenStateGuard forbiddenStateGuard;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY_PREFIX = "ondc:fulfillment:state:";
    private static final Duration STATE_TTL = Duration.ofDays(7); // Keep state for 7 days

    /**
     * Transitions an order to a new fulfillment state.
     *
     * @param transactionId ONDC transaction ID
     * @param newState      target state
     * @return the new state after transition
     */
    public OndcFulfillmentState transition(String transactionId, OndcFulfillmentState newState) {
        OndcFulfillmentState currentState = getCurrentState(transactionId);

        // Skip if already at this state (idempotent)
        if (currentState == newState) {
            log.debug("Order {} already at state {}, skipping", transactionId, newState.getOndcValue());
            return currentState;
        }

        forbiddenStateGuard.validateTransition(currentState, newState);

        String redisKey = REDIS_KEY_PREFIX + transactionId;
        redisTemplate.opsForValue().set(redisKey, newState.name(), STATE_TTL);
        
        log.info("Order {} transitioned: {} → {}",
                transactionId, currentState.getOndcValue(), newState.getOndcValue());

        return newState;
    }

    /**
     * Gets the current fulfillment state for an order.
     */
    public OndcFulfillmentState getCurrentState(String transactionId) {
        String redisKey = REDIS_KEY_PREFIX + transactionId;
        String stateStr = redisTemplate.opsForValue().get(redisKey);
        
        if (stateStr == null) {
            return OndcFulfillmentState.PENDING;
        }
        
        try {
            return OndcFulfillmentState.valueOf(stateStr);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid fulfillment state in Redis for transaction {}: {}", transactionId, stateStr);
            return OndcFulfillmentState.PENDING;
        }
    }

    /**
     * Removes state tracking for a completed/cancelled order.
     */
    public void removeState(String transactionId) {
        String redisKey = REDIS_KEY_PREFIX + transactionId;
        redisTemplate.delete(redisKey);
    }
}

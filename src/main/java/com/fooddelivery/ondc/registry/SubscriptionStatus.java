package com.fooddelivery.ondc.registry;

/**
 * Tracks the lifecycle of ONDC network subscription.
 */
public enum SubscriptionStatus {
    INITIATED,
    CHALLENGE_RECEIVED,
    CHALLENGE_DECRYPTED,
    ACTIVE,
    SUSPENDED,
    EXPIRED
}

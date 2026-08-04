package com.fooddelivery.ondc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables Spring's asynchronous method execution capability.
 * Required for @Async methods like Beckn callbacks.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}

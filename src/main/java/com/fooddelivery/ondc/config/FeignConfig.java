package com.fooddelivery.ondc.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign client configuration. Actual clients are declared in the client package.
 * Security interceptor from CommonLibrary propagates X-User-Id and X-User-Roles.
 */
@Configuration
@EnableFeignClients(basePackages = "com.fooddelivery.ondc.client")
public class FeignConfig {
}

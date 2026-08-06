package com.fooddelivery.ondc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized ONDC configuration bound from ConfigService.
 * All sensitive values (keys, subscriber IDs) are resolved from environment variables.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ondc")
public class OndcProperties {

    private String subscriberId;
    private String subscriberUrl;
    private String uniqueKeyId;
    private String domain = "ONDC:RET11";
    private String city = "std:080";
    private String country = "IND";

    private Registry registry = new Registry();
    private Crypto crypto = new Crypto();
    private Callback callback = new Callback();
    private Fulfillment fulfillment = new Fulfillment();

    private int clockSkewToleranceSeconds = 60;

    @Getter
    @Setter
    public static class Registry {
        private String url = "https://staging.registry.ondc.org";
        private String gatewayUrl = "https://staging.gateway.ondc.org";
        private int lookupCacheTtlSeconds = 3600;
        private String verificationId = "default-verification-id";
    }

    @Getter
    @Setter
    public static class Crypto {
        private String signingPrivateKey;
        private String signingPublicKey;
        private String encryptionPrivateKey;
        private String encryptionPublicKey;
        private String ondcProductionPublicKey = "MCowBQYDK2VuAyEAvVEyZY91O2yV8w8/CAwVDAnqIZDJJUPdLUUKwLo3K0M=";
    }

    @Getter
    @Setter
    public static class Callback {
        private int retryMaxAttempts = 3;
        private long retryBackoffMs = 1000;
    }

    @Getter
    @Setter
    public static class Fulfillment {
        private int maxDeliveryRadiusKm = 7;
        private boolean selfPickupEnabled = true;
    }
}

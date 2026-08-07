package com.fooddelivery.ondc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalized ONDC configuration bound from ConfigService.
 * All sensitive values (keys, subscriber IDs) are resolved from environment variables.
 */
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


    public static class Registry {
        private String url = "https://staging.registry.ondc.org";
        private String gatewayUrl = "https://staging.gateway.ondc.org";
        private int lookupCacheTtlSeconds = 3600;
        private String verificationId = "default-verification-id";

        @java.lang.SuppressWarnings("all")
        public String getUrl() {
            return this.url;
        }

        @java.lang.SuppressWarnings("all")
        public String getGatewayUrl() {
            return this.gatewayUrl;
        }

        @java.lang.SuppressWarnings("all")
        public int getLookupCacheTtlSeconds() {
            return this.lookupCacheTtlSeconds;
        }

        @java.lang.SuppressWarnings("all")
        public String getVerificationId() {
            return this.verificationId;
        }

        @java.lang.SuppressWarnings("all")
        public void setUrl(final String url) {
            this.url = url;
        }

        @java.lang.SuppressWarnings("all")
        public void setGatewayUrl(final String gatewayUrl) {
            this.gatewayUrl = gatewayUrl;
        }

        @java.lang.SuppressWarnings("all")
        public void setLookupCacheTtlSeconds(final int lookupCacheTtlSeconds) {
            this.lookupCacheTtlSeconds = lookupCacheTtlSeconds;
        }

        @java.lang.SuppressWarnings("all")
        public void setVerificationId(final String verificationId) {
            this.verificationId = verificationId;
        }
    }


    public static class Crypto {
        private String signingPrivateKey;
        private String signingPublicKey;
        private String encryptionPrivateKey;
        private String encryptionPublicKey;
        private String ondcProductionPublicKey = "MCowBQYDK2VuAyEAvVEyZY91O2yV8w8/CAwVDAnqIZDJJUPdLUUKwLo3K0M=";

        @java.lang.SuppressWarnings("all")
        public String getSigningPrivateKey() {
            return this.signingPrivateKey;
        }

        @java.lang.SuppressWarnings("all")
        public String getSigningPublicKey() {
            return this.signingPublicKey;
        }

        @java.lang.SuppressWarnings("all")
        public String getEncryptionPrivateKey() {
            return this.encryptionPrivateKey;
        }

        @java.lang.SuppressWarnings("all")
        public String getEncryptionPublicKey() {
            return this.encryptionPublicKey;
        }

        @java.lang.SuppressWarnings("all")
        public String getOndcProductionPublicKey() {
            return this.ondcProductionPublicKey;
        }

        @java.lang.SuppressWarnings("all")
        public void setSigningPrivateKey(final String signingPrivateKey) {
            this.signingPrivateKey = signingPrivateKey;
        }

        @java.lang.SuppressWarnings("all")
        public void setSigningPublicKey(final String signingPublicKey) {
            this.signingPublicKey = signingPublicKey;
        }

        @java.lang.SuppressWarnings("all")
        public void setEncryptionPrivateKey(final String encryptionPrivateKey) {
            this.encryptionPrivateKey = encryptionPrivateKey;
        }

        @java.lang.SuppressWarnings("all")
        public void setEncryptionPublicKey(final String encryptionPublicKey) {
            this.encryptionPublicKey = encryptionPublicKey;
        }

        @java.lang.SuppressWarnings("all")
        public void setOndcProductionPublicKey(final String ondcProductionPublicKey) {
            this.ondcProductionPublicKey = ondcProductionPublicKey;
        }
    }


    public static class Callback {
        private int retryMaxAttempts = 3;
        private long retryBackoffMs = 1000;

        @java.lang.SuppressWarnings("all")
        public int getRetryMaxAttempts() {
            return this.retryMaxAttempts;
        }

        @java.lang.SuppressWarnings("all")
        public long getRetryBackoffMs() {
            return this.retryBackoffMs;
        }

        @java.lang.SuppressWarnings("all")
        public void setRetryMaxAttempts(final int retryMaxAttempts) {
            this.retryMaxAttempts = retryMaxAttempts;
        }

        @java.lang.SuppressWarnings("all")
        public void setRetryBackoffMs(final long retryBackoffMs) {
            this.retryBackoffMs = retryBackoffMs;
        }
    }


    public static class Fulfillment {
        private int maxDeliveryRadiusKm = 7;
        private boolean selfPickupEnabled = true;

        @java.lang.SuppressWarnings("all")
        public int getMaxDeliveryRadiusKm() {
            return this.maxDeliveryRadiusKm;
        }

        @java.lang.SuppressWarnings("all")
        public boolean isSelfPickupEnabled() {
            return this.selfPickupEnabled;
        }

        @java.lang.SuppressWarnings("all")
        public void setMaxDeliveryRadiusKm(final int maxDeliveryRadiusKm) {
            this.maxDeliveryRadiusKm = maxDeliveryRadiusKm;
        }

        @java.lang.SuppressWarnings("all")
        public void setSelfPickupEnabled(final boolean selfPickupEnabled) {
            this.selfPickupEnabled = selfPickupEnabled;
        }
    }

    @java.lang.SuppressWarnings("all")
    public String getSubscriberId() {
        return this.subscriberId;
    }

    @java.lang.SuppressWarnings("all")
    public String getSubscriberUrl() {
        return this.subscriberUrl;
    }

    @java.lang.SuppressWarnings("all")
    public String getUniqueKeyId() {
        return this.uniqueKeyId;
    }

    @java.lang.SuppressWarnings("all")
    public String getDomain() {
        return this.domain;
    }

    @java.lang.SuppressWarnings("all")
    public String getCity() {
        return this.city;
    }

    @java.lang.SuppressWarnings("all")
    public String getCountry() {
        return this.country;
    }

    @java.lang.SuppressWarnings("all")
    public Registry getRegistry() {
        return this.registry;
    }

    @java.lang.SuppressWarnings("all")
    public Crypto getCrypto() {
        return this.crypto;
    }

    @java.lang.SuppressWarnings("all")
    public Callback getCallback() {
        return this.callback;
    }

    @java.lang.SuppressWarnings("all")
    public Fulfillment getFulfillment() {
        return this.fulfillment;
    }

    @java.lang.SuppressWarnings("all")
    public int getClockSkewToleranceSeconds() {
        return this.clockSkewToleranceSeconds;
    }

    @java.lang.SuppressWarnings("all")
    public void setSubscriberId(final String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @java.lang.SuppressWarnings("all")
    public void setSubscriberUrl(final String subscriberUrl) {
        this.subscriberUrl = subscriberUrl;
    }

    @java.lang.SuppressWarnings("all")
    public void setUniqueKeyId(final String uniqueKeyId) {
        this.uniqueKeyId = uniqueKeyId;
    }

    @java.lang.SuppressWarnings("all")
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    @java.lang.SuppressWarnings("all")
    public void setCity(final String city) {
        this.city = city;
    }

    @java.lang.SuppressWarnings("all")
    public void setCountry(final String country) {
        this.country = country;
    }

    @java.lang.SuppressWarnings("all")
    public void setRegistry(final Registry registry) {
        this.registry = registry;
    }

    @java.lang.SuppressWarnings("all")
    public void setCrypto(final Crypto crypto) {
        this.crypto = crypto;
    }

    @java.lang.SuppressWarnings("all")
    public void setCallback(final Callback callback) {
        this.callback = callback;
    }

    @java.lang.SuppressWarnings("all")
    public void setFulfillment(final Fulfillment fulfillment) {
        this.fulfillment = fulfillment;
    }

    @java.lang.SuppressWarnings("all")
    public void setClockSkewToleranceSeconds(final int clockSkewToleranceSeconds) {
        this.clockSkewToleranceSeconds = clockSkewToleranceSeconds;
    }
}

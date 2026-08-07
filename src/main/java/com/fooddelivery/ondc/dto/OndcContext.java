package com.fooddelivery.ondc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Standard ONDC Beckn context object included in every request/response.
 * Maps to the Beckn Protocol context schema.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OndcContext {
    private String domain;
    private String action;
    private String country;
    private String city;
    @JsonProperty("core_version")
    private String coreVersion;
    @JsonProperty("bap_id")
    private String bapId;
    @JsonProperty("bap_uri")
    private String bapUri;
    @JsonProperty("bpp_id")
    private String bppId;
    @JsonProperty("bpp_uri")
    private String bppUri;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("message_id")
    private String messageId;
    private String timestamp;
    private String ttl;
    @JsonProperty("key")
    private String key;


    @java.lang.SuppressWarnings("all")
    public static class OndcContextBuilder {
        @java.lang.SuppressWarnings("all")
        private String domain;
        @java.lang.SuppressWarnings("all")
        private String action;
        @java.lang.SuppressWarnings("all")
        private String country;
        @java.lang.SuppressWarnings("all")
        private String city;
        @java.lang.SuppressWarnings("all")
        private String coreVersion;
        @java.lang.SuppressWarnings("all")
        private String bapId;
        @java.lang.SuppressWarnings("all")
        private String bapUri;
        @java.lang.SuppressWarnings("all")
        private String bppId;
        @java.lang.SuppressWarnings("all")
        private String bppUri;
        @java.lang.SuppressWarnings("all")
        private String transactionId;
        @java.lang.SuppressWarnings("all")
        private String messageId;
        @java.lang.SuppressWarnings("all")
        private String timestamp;
        @java.lang.SuppressWarnings("all")
        private String ttl;
        @java.lang.SuppressWarnings("all")
        private String key;

        @java.lang.SuppressWarnings("all")
        OndcContextBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder domain(final String domain) {
            this.domain = domain;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder action(final String action) {
            this.action = action;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder country(final String country) {
            this.country = country;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder city(final String city) {
            this.city = city;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("core_version")
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder coreVersion(final String coreVersion) {
            this.coreVersion = coreVersion;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("bap_id")
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder bapId(final String bapId) {
            this.bapId = bapId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("bap_uri")
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder bapUri(final String bapUri) {
            this.bapUri = bapUri;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("bpp_id")
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder bppId(final String bppId) {
            this.bppId = bppId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("bpp_uri")
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder bppUri(final String bppUri) {
            this.bppUri = bppUri;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("transaction_id")
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder transactionId(final String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("message_id")
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder messageId(final String messageId) {
            this.messageId = messageId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder timestamp(final String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder ttl(final String ttl) {
            this.ttl = ttl;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @JsonProperty("key")
        @java.lang.SuppressWarnings("all")
        public OndcContext.OndcContextBuilder key(final String key) {
            this.key = key;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcContext build() {
            return new OndcContext(this.domain, this.action, this.country, this.city, this.coreVersion, this.bapId, this.bapUri, this.bppId, this.bppUri, this.transactionId, this.messageId, this.timestamp, this.ttl, this.key);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcContext.OndcContextBuilder(domain=" + this.domain + ", action=" + this.action + ", country=" + this.country + ", city=" + this.city + ", coreVersion=" + this.coreVersion + ", bapId=" + this.bapId + ", bapUri=" + this.bapUri + ", bppId=" + this.bppId + ", bppUri=" + this.bppUri + ", transactionId=" + this.transactionId + ", messageId=" + this.messageId + ", timestamp=" + this.timestamp + ", ttl=" + this.ttl + ", key=" + this.key + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcContext.OndcContextBuilder builder() {
        return new OndcContext.OndcContextBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public String getDomain() {
        return this.domain;
    }

    @java.lang.SuppressWarnings("all")
    public String getAction() {
        return this.action;
    }

    @java.lang.SuppressWarnings("all")
    public String getCountry() {
        return this.country;
    }

    @java.lang.SuppressWarnings("all")
    public String getCity() {
        return this.city;
    }

    @java.lang.SuppressWarnings("all")
    public String getCoreVersion() {
        return this.coreVersion;
    }

    @java.lang.SuppressWarnings("all")
    public String getBapId() {
        return this.bapId;
    }

    @java.lang.SuppressWarnings("all")
    public String getBapUri() {
        return this.bapUri;
    }

    @java.lang.SuppressWarnings("all")
    public String getBppId() {
        return this.bppId;
    }

    @java.lang.SuppressWarnings("all")
    public String getBppUri() {
        return this.bppUri;
    }

    @java.lang.SuppressWarnings("all")
    public String getTransactionId() {
        return this.transactionId;
    }

    @java.lang.SuppressWarnings("all")
    public String getMessageId() {
        return this.messageId;
    }

    @java.lang.SuppressWarnings("all")
    public String getTimestamp() {
        return this.timestamp;
    }

    @java.lang.SuppressWarnings("all")
    public String getTtl() {
        return this.ttl;
    }

    @java.lang.SuppressWarnings("all")
    public String getKey() {
        return this.key;
    }

    @java.lang.SuppressWarnings("all")
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    @java.lang.SuppressWarnings("all")
    public void setAction(final String action) {
        this.action = action;
    }

    @java.lang.SuppressWarnings("all")
    public void setCountry(final String country) {
        this.country = country;
    }

    @java.lang.SuppressWarnings("all")
    public void setCity(final String city) {
        this.city = city;
    }

    @JsonProperty("core_version")
    @java.lang.SuppressWarnings("all")
    public void setCoreVersion(final String coreVersion) {
        this.coreVersion = coreVersion;
    }

    @JsonProperty("bap_id")
    @java.lang.SuppressWarnings("all")
    public void setBapId(final String bapId) {
        this.bapId = bapId;
    }

    @JsonProperty("bap_uri")
    @java.lang.SuppressWarnings("all")
    public void setBapUri(final String bapUri) {
        this.bapUri = bapUri;
    }

    @JsonProperty("bpp_id")
    @java.lang.SuppressWarnings("all")
    public void setBppId(final String bppId) {
        this.bppId = bppId;
    }

    @JsonProperty("bpp_uri")
    @java.lang.SuppressWarnings("all")
    public void setBppUri(final String bppUri) {
        this.bppUri = bppUri;
    }

    @JsonProperty("transaction_id")
    @java.lang.SuppressWarnings("all")
    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("message_id")
    @java.lang.SuppressWarnings("all")
    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    @java.lang.SuppressWarnings("all")
    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    @java.lang.SuppressWarnings("all")
    public void setTtl(final String ttl) {
        this.ttl = ttl;
    }

    @JsonProperty("key")
    @java.lang.SuppressWarnings("all")
    public void setKey(final String key) {
        this.key = key;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OndcContext)) return false;
        final OndcContext other = (OndcContext) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$domain = this.getDomain();
        final java.lang.Object other$domain = other.getDomain();
        if (this$domain == null ? other$domain != null : !this$domain.equals(other$domain)) return false;
        final java.lang.Object this$action = this.getAction();
        final java.lang.Object other$action = other.getAction();
        if (this$action == null ? other$action != null : !this$action.equals(other$action)) return false;
        final java.lang.Object this$country = this.getCountry();
        final java.lang.Object other$country = other.getCountry();
        if (this$country == null ? other$country != null : !this$country.equals(other$country)) return false;
        final java.lang.Object this$city = this.getCity();
        final java.lang.Object other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) return false;
        final java.lang.Object this$coreVersion = this.getCoreVersion();
        final java.lang.Object other$coreVersion = other.getCoreVersion();
        if (this$coreVersion == null ? other$coreVersion != null : !this$coreVersion.equals(other$coreVersion)) return false;
        final java.lang.Object this$bapId = this.getBapId();
        final java.lang.Object other$bapId = other.getBapId();
        if (this$bapId == null ? other$bapId != null : !this$bapId.equals(other$bapId)) return false;
        final java.lang.Object this$bapUri = this.getBapUri();
        final java.lang.Object other$bapUri = other.getBapUri();
        if (this$bapUri == null ? other$bapUri != null : !this$bapUri.equals(other$bapUri)) return false;
        final java.lang.Object this$bppId = this.getBppId();
        final java.lang.Object other$bppId = other.getBppId();
        if (this$bppId == null ? other$bppId != null : !this$bppId.equals(other$bppId)) return false;
        final java.lang.Object this$bppUri = this.getBppUri();
        final java.lang.Object other$bppUri = other.getBppUri();
        if (this$bppUri == null ? other$bppUri != null : !this$bppUri.equals(other$bppUri)) return false;
        final java.lang.Object this$transactionId = this.getTransactionId();
        final java.lang.Object other$transactionId = other.getTransactionId();
        if (this$transactionId == null ? other$transactionId != null : !this$transactionId.equals(other$transactionId)) return false;
        final java.lang.Object this$messageId = this.getMessageId();
        final java.lang.Object other$messageId = other.getMessageId();
        if (this$messageId == null ? other$messageId != null : !this$messageId.equals(other$messageId)) return false;
        final java.lang.Object this$timestamp = this.getTimestamp();
        final java.lang.Object other$timestamp = other.getTimestamp();
        if (this$timestamp == null ? other$timestamp != null : !this$timestamp.equals(other$timestamp)) return false;
        final java.lang.Object this$ttl = this.getTtl();
        final java.lang.Object other$ttl = other.getTtl();
        if (this$ttl == null ? other$ttl != null : !this$ttl.equals(other$ttl)) return false;
        final java.lang.Object this$key = this.getKey();
        final java.lang.Object other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof OndcContext;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $domain = this.getDomain();
        result = result * PRIME + ($domain == null ? 43 : $domain.hashCode());
        final java.lang.Object $action = this.getAction();
        result = result * PRIME + ($action == null ? 43 : $action.hashCode());
        final java.lang.Object $country = this.getCountry();
        result = result * PRIME + ($country == null ? 43 : $country.hashCode());
        final java.lang.Object $city = this.getCity();
        result = result * PRIME + ($city == null ? 43 : $city.hashCode());
        final java.lang.Object $coreVersion = this.getCoreVersion();
        result = result * PRIME + ($coreVersion == null ? 43 : $coreVersion.hashCode());
        final java.lang.Object $bapId = this.getBapId();
        result = result * PRIME + ($bapId == null ? 43 : $bapId.hashCode());
        final java.lang.Object $bapUri = this.getBapUri();
        result = result * PRIME + ($bapUri == null ? 43 : $bapUri.hashCode());
        final java.lang.Object $bppId = this.getBppId();
        result = result * PRIME + ($bppId == null ? 43 : $bppId.hashCode());
        final java.lang.Object $bppUri = this.getBppUri();
        result = result * PRIME + ($bppUri == null ? 43 : $bppUri.hashCode());
        final java.lang.Object $transactionId = this.getTransactionId();
        result = result * PRIME + ($transactionId == null ? 43 : $transactionId.hashCode());
        final java.lang.Object $messageId = this.getMessageId();
        result = result * PRIME + ($messageId == null ? 43 : $messageId.hashCode());
        final java.lang.Object $timestamp = this.getTimestamp();
        result = result * PRIME + ($timestamp == null ? 43 : $timestamp.hashCode());
        final java.lang.Object $ttl = this.getTtl();
        result = result * PRIME + ($ttl == null ? 43 : $ttl.hashCode());
        final java.lang.Object $key = this.getKey();
        result = result * PRIME + ($key == null ? 43 : $key.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "OndcContext(domain=" + this.getDomain() + ", action=" + this.getAction() + ", country=" + this.getCountry() + ", city=" + this.getCity() + ", coreVersion=" + this.getCoreVersion() + ", bapId=" + this.getBapId() + ", bapUri=" + this.getBapUri() + ", bppId=" + this.getBppId() + ", bppUri=" + this.getBppUri() + ", transactionId=" + this.getTransactionId() + ", messageId=" + this.getMessageId() + ", timestamp=" + this.getTimestamp() + ", ttl=" + this.getTtl() + ", key=" + this.getKey() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public OndcContext() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcContext(final String domain, final String action, final String country, final String city, final String coreVersion, final String bapId, final String bapUri, final String bppId, final String bppUri, final String transactionId, final String messageId, final String timestamp, final String ttl, final String key) {
        this.domain = domain;
        this.action = action;
        this.country = country;
        this.city = city;
        this.coreVersion = coreVersion;
        this.bapId = bapId;
        this.bapUri = bapUri;
        this.bppId = bppId;
        this.bppUri = bppUri;
        this.transactionId = transactionId;
        this.messageId = messageId;
        this.timestamp = timestamp;
        this.ttl = ttl;
        this.key = key;
    }
}

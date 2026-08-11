package com.fooddelivery.ondc.entity;

import com.fooddelivery.ondc.registry.SubscriptionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores ONDC network participant identity, cryptographic keys, and subscription status.
 */
@Entity
@Table(name = "ondc_network_participants")
public class OndcNetworkParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "subscriber_id", nullable = false, unique = true)
    private String subscriberId;
    @Column(name = "unique_key_id", nullable = false)
    private String uniqueKeyId;
    @Column(name = "signing_public_key", nullable = false, length = 1024)
    private String signingPublicKey;
    @Column(name = "encryption_public_key", nullable = false, length = 1024)
    private String encryptionPublicKey;
    @Column(name = "subscriber_url", nullable = false)
    private String subscriberUrl;
    @Column(name = "ops_no")
    private Integer opsNo;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;
    @Column(name = "domain")
    private String domain;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Version
    private Long version;


    @java.lang.SuppressWarnings("all")
    public static class OndcNetworkParticipantBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private String subscriberId;
        @java.lang.SuppressWarnings("all")
        private String uniqueKeyId;
        @java.lang.SuppressWarnings("all")
        private String signingPublicKey;
        @java.lang.SuppressWarnings("all")
        private String encryptionPublicKey;
        @java.lang.SuppressWarnings("all")
        private String subscriberUrl;
        @java.lang.SuppressWarnings("all")
        private Integer opsNo;
        @java.lang.SuppressWarnings("all")
        private SubscriptionStatus status;
        @java.lang.SuppressWarnings("all")
        private String domain;
        @java.lang.SuppressWarnings("all")
        private String city;
        @java.lang.SuppressWarnings("all")
        private String country;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime createdAt;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime updatedAt;
        @java.lang.SuppressWarnings("all")
        private Long version;

        @java.lang.SuppressWarnings("all")
        OndcNetworkParticipantBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder subscriberId(final String subscriberId) {
            this.subscriberId = subscriberId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder uniqueKeyId(final String uniqueKeyId) {
            this.uniqueKeyId = uniqueKeyId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder signingPublicKey(final String signingPublicKey) {
            this.signingPublicKey = signingPublicKey;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder encryptionPublicKey(final String encryptionPublicKey) {
            this.encryptionPublicKey = encryptionPublicKey;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder subscriberUrl(final String subscriberUrl) {
            this.subscriberUrl = subscriberUrl;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder opsNo(final Integer opsNo) {
            this.opsNo = opsNo;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder status(final SubscriptionStatus status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder domain(final String domain) {
            this.domain = domain;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder city(final String city) {
            this.city = city;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder country(final String country) {
            this.country = country;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant.OndcNetworkParticipantBuilder version(final Long version) {
            this.version = version;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcNetworkParticipant build() {
            return new OndcNetworkParticipant(this.id, this.subscriberId, this.uniqueKeyId, this.signingPublicKey, this.encryptionPublicKey, this.subscriberUrl, this.opsNo, this.status, this.domain, this.city, this.country, this.createdAt, this.updatedAt, this.version);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcNetworkParticipant.OndcNetworkParticipantBuilder(id=" + this.id + ", subscriberId=" + this.subscriberId + ", uniqueKeyId=" + this.uniqueKeyId + ", signingPublicKey=" + this.signingPublicKey + ", encryptionPublicKey=" + this.encryptionPublicKey + ", subscriberUrl=" + this.subscriberUrl + ", opsNo=" + this.opsNo + ", status=" + this.status + ", domain=" + this.domain + ", city=" + this.city + ", country=" + this.country + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", version=" + this.version + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcNetworkParticipant.OndcNetworkParticipantBuilder builder() {
        return new OndcNetworkParticipant.OndcNetworkParticipantBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getSubscriberId() {
        return this.subscriberId;
    }

    @java.lang.SuppressWarnings("all")
    public String getUniqueKeyId() {
        return this.uniqueKeyId;
    }

    @java.lang.SuppressWarnings("all")
    public String getSigningPublicKey() {
        return this.signingPublicKey;
    }

    @java.lang.SuppressWarnings("all")
    public String getEncryptionPublicKey() {
        return this.encryptionPublicKey;
    }

    @java.lang.SuppressWarnings("all")
    public String getSubscriberUrl() {
        return this.subscriberUrl;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getOpsNo() {
        return this.opsNo;
    }

    @java.lang.SuppressWarnings("all")
    public SubscriptionStatus getStatus() {
        return this.status;
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
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @java.lang.SuppressWarnings("all")
    public Long getVersion() {
        return this.version;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final UUID id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setSubscriberId(final String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @java.lang.SuppressWarnings("all")
    public void setUniqueKeyId(final String uniqueKeyId) {
        this.uniqueKeyId = uniqueKeyId;
    }

    @java.lang.SuppressWarnings("all")
    public void setSigningPublicKey(final String signingPublicKey) {
        this.signingPublicKey = signingPublicKey;
    }

    @java.lang.SuppressWarnings("all")
    public void setEncryptionPublicKey(final String encryptionPublicKey) {
        this.encryptionPublicKey = encryptionPublicKey;
    }

    @java.lang.SuppressWarnings("all")
    public void setSubscriberUrl(final String subscriberUrl) {
        this.subscriberUrl = subscriberUrl;
    }

    @java.lang.SuppressWarnings("all")
    public void setOpsNo(final Integer opsNo) {
        this.opsNo = opsNo;
    }

    @java.lang.SuppressWarnings("all")
    public void setStatus(final SubscriptionStatus status) {
        this.status = status;
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
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setVersion(final Long version) {
        this.version = version;
    }

    @java.lang.SuppressWarnings("all")
    public OndcNetworkParticipant() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcNetworkParticipant(final UUID id, final String subscriberId, final String uniqueKeyId, final String signingPublicKey, final String encryptionPublicKey, final String subscriberUrl, final Integer opsNo, final SubscriptionStatus status, final String domain, final String city, final String country, final LocalDateTime createdAt, final LocalDateTime updatedAt, final Long version) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.uniqueKeyId = uniqueKeyId;
        this.signingPublicKey = signingPublicKey;
        this.encryptionPublicKey = encryptionPublicKey;
        this.subscriberUrl = subscriberUrl;
        this.opsNo = opsNo;
        this.status = status;
        this.domain = domain;
        this.city = city;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}

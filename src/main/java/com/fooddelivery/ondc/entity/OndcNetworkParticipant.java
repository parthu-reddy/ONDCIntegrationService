package com.fooddelivery.ondc.entity;

import com.fooddelivery.ondc.registry.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores ONDC network participant identity, cryptographic keys, and subscription status.
 */
@Entity
@Table(name = "ondc_network_participants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OndcNetworkParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "subscriber_id", nullable = false, unique = true)
    private String subscriberId;

    @Column(name = "unique_key_id", nullable = false)
    private String uniqueKeyId;

    @Column(name = "signing_public_key", nullable = false, length = 500)
    private String signingPublicKey;

    @Column(name = "encryption_public_key", nullable = false, length = 500)
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
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;
}

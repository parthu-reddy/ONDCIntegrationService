package com.fooddelivery.ondc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Tracks ONDC RSF 2.0 settlement state per order.
 * Ensures idempotent settlement processing (no double-credit/debit).
 */
@Entity
@Table(name = "ondc_settlements",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ondc_transaction_id", "settlement_type"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OndcSettlementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ondc_transaction_id", nullable = false)
    private String ondcTransactionId;

    @Column(name = "internal_order_id")
    private UUID internalOrderId;

    @Column(name = "settlement_type", nullable = false)
    private String settlementType; // COLLECT, SETTLE, REFUND

    @Column(name = "collector_id")
    private String collectorId;

    @Column(name = "receiver_id")
    private String receiverId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, MATCHED, SETTLED, FAILED, REFUNDED

    @Column(name = "settlement_reference")
    private String settlementReference;

    @Column(name = "error_details")
    private String errorDetails;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;
}

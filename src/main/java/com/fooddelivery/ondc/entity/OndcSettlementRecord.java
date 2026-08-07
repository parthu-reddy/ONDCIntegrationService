package com.fooddelivery.ondc.entity;

import jakarta.persistence.*;
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
@Table(name = "ondc_settlements", uniqueConstraints = @UniqueConstraint(columnNames = {"ondc_transaction_id", "settlement_type"}))
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


    @java.lang.SuppressWarnings("all")
    public static class OndcSettlementRecordBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private String ondcTransactionId;
        @java.lang.SuppressWarnings("all")
        private UUID internalOrderId;
        @java.lang.SuppressWarnings("all")
        private String settlementType;
        @java.lang.SuppressWarnings("all")
        private String collectorId;
        @java.lang.SuppressWarnings("all")
        private String receiverId;
        @java.lang.SuppressWarnings("all")
        private BigDecimal amount;
        @java.lang.SuppressWarnings("all")
        private String currency;
        @java.lang.SuppressWarnings("all")
        private String status;
        @java.lang.SuppressWarnings("all")
        private String settlementReference;
        @java.lang.SuppressWarnings("all")
        private String errorDetails;
        @java.lang.SuppressWarnings("all")
        private Instant createdAt;
        @java.lang.SuppressWarnings("all")
        private Instant updatedAt;
        @java.lang.SuppressWarnings("all")
        private Long version;

        @java.lang.SuppressWarnings("all")
        OndcSettlementRecordBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder ondcTransactionId(final String ondcTransactionId) {
            this.ondcTransactionId = ondcTransactionId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder internalOrderId(final UUID internalOrderId) {
            this.internalOrderId = internalOrderId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder settlementType(final String settlementType) {
            this.settlementType = settlementType;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder collectorId(final String collectorId) {
            this.collectorId = collectorId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder receiverId(final String receiverId) {
            this.receiverId = receiverId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder amount(final BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder currency(final String currency) {
            this.currency = currency;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder status(final String status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder settlementReference(final String settlementReference) {
            this.settlementReference = settlementReference;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder errorDetails(final String errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder createdAt(final Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder updatedAt(final Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord.OndcSettlementRecordBuilder version(final Long version) {
            this.version = version;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcSettlementRecord build() {
            return new OndcSettlementRecord(this.id, this.ondcTransactionId, this.internalOrderId, this.settlementType, this.collectorId, this.receiverId, this.amount, this.currency, this.status, this.settlementReference, this.errorDetails, this.createdAt, this.updatedAt, this.version);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcSettlementRecord.OndcSettlementRecordBuilder(id=" + this.id + ", ondcTransactionId=" + this.ondcTransactionId + ", internalOrderId=" + this.internalOrderId + ", settlementType=" + this.settlementType + ", collectorId=" + this.collectorId + ", receiverId=" + this.receiverId + ", amount=" + this.amount + ", currency=" + this.currency + ", status=" + this.status + ", settlementReference=" + this.settlementReference + ", errorDetails=" + this.errorDetails + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", version=" + this.version + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcSettlementRecord.OndcSettlementRecordBuilder builder() {
        return new OndcSettlementRecord.OndcSettlementRecordBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public String getOndcTransactionId() {
        return this.ondcTransactionId;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getInternalOrderId() {
        return this.internalOrderId;
    }

    @java.lang.SuppressWarnings("all")
    public String getSettlementType() {
        return this.settlementType;
    }

    @java.lang.SuppressWarnings("all")
    public String getCollectorId() {
        return this.collectorId;
    }

    @java.lang.SuppressWarnings("all")
    public String getReceiverId() {
        return this.receiverId;
    }

    @java.lang.SuppressWarnings("all")
    public BigDecimal getAmount() {
        return this.amount;
    }

    @java.lang.SuppressWarnings("all")
    public String getCurrency() {
        return this.currency;
    }

    @java.lang.SuppressWarnings("all")
    public String getStatus() {
        return this.status;
    }

    @java.lang.SuppressWarnings("all")
    public String getSettlementReference() {
        return this.settlementReference;
    }

    @java.lang.SuppressWarnings("all")
    public String getErrorDetails() {
        return this.errorDetails;
    }

    @java.lang.SuppressWarnings("all")
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public Instant getUpdatedAt() {
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
    public void setOndcTransactionId(final String ondcTransactionId) {
        this.ondcTransactionId = ondcTransactionId;
    }

    @java.lang.SuppressWarnings("all")
    public void setInternalOrderId(final UUID internalOrderId) {
        this.internalOrderId = internalOrderId;
    }

    @java.lang.SuppressWarnings("all")
    public void setSettlementType(final String settlementType) {
        this.settlementType = settlementType;
    }

    @java.lang.SuppressWarnings("all")
    public void setCollectorId(final String collectorId) {
        this.collectorId = collectorId;
    }

    @java.lang.SuppressWarnings("all")
    public void setReceiverId(final String receiverId) {
        this.receiverId = receiverId;
    }

    @java.lang.SuppressWarnings("all")
    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    @java.lang.SuppressWarnings("all")
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    @java.lang.SuppressWarnings("all")
    public void setStatus(final String status) {
        this.status = status;
    }

    @java.lang.SuppressWarnings("all")
    public void setSettlementReference(final String settlementReference) {
        this.settlementReference = settlementReference;
    }

    @java.lang.SuppressWarnings("all")
    public void setErrorDetails(final String errorDetails) {
        this.errorDetails = errorDetails;
    }

    @java.lang.SuppressWarnings("all")
    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setUpdatedAt(final Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setVersion(final Long version) {
        this.version = version;
    }

    @java.lang.SuppressWarnings("all")
    public OndcSettlementRecord() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcSettlementRecord(final UUID id, final String ondcTransactionId, final UUID internalOrderId, final String settlementType, final String collectorId, final String receiverId, final BigDecimal amount, final String currency, final String status, final String settlementReference, final String errorDetails, final Instant createdAt, final Instant updatedAt, final Long version) {
        this.id = id;
        this.ondcTransactionId = ondcTransactionId;
        this.internalOrderId = internalOrderId;
        this.settlementType = settlementType;
        this.collectorId = collectorId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.settlementReference = settlementReference;
        this.errorDetails = errorDetails;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}

package com.fooddelivery.ondc.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Audit trail of catalog syndication events and incremental delta pushes.
 */
@Entity
@Table(name = "ondc_catalog_sync_log")
public class OndcCatalogSyncLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "outlet_id", nullable = false)
    private UUID outletId;
    @Column(name = "sync_type", nullable = false)
    private String syncType; // FULL, INCREMENTAL
    @Column(name = "items_synced")
    private Integer itemsSynced;
    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, FAILED, PARTIAL
    @Column(name = "error_details")
    private String errorDetails;
    @Column(name = "triggered_by")
    private String triggeredBy; // SEARCH_REQUEST, STOCK_CHANGE, PRICE_CHANGE, SCHEDULED
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;


    @java.lang.SuppressWarnings("all")
    public static class OndcCatalogSyncLogBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private UUID outletId;
        @java.lang.SuppressWarnings("all")
        private String syncType;
        @java.lang.SuppressWarnings("all")
        private Integer itemsSynced;
        @java.lang.SuppressWarnings("all")
        private String status;
        @java.lang.SuppressWarnings("all")
        private String errorDetails;
        @java.lang.SuppressWarnings("all")
        private String triggeredBy;
        @java.lang.SuppressWarnings("all")
        private Instant createdAt;

        @java.lang.SuppressWarnings("all")
        OndcCatalogSyncLogBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog.OndcCatalogSyncLogBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog.OndcCatalogSyncLogBuilder outletId(final UUID outletId) {
            this.outletId = outletId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog.OndcCatalogSyncLogBuilder syncType(final String syncType) {
            this.syncType = syncType;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog.OndcCatalogSyncLogBuilder itemsSynced(final Integer itemsSynced) {
            this.itemsSynced = itemsSynced;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog.OndcCatalogSyncLogBuilder status(final String status) {
            this.status = status;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog.OndcCatalogSyncLogBuilder errorDetails(final String errorDetails) {
            this.errorDetails = errorDetails;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog.OndcCatalogSyncLogBuilder triggeredBy(final String triggeredBy) {
            this.triggeredBy = triggeredBy;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog.OndcCatalogSyncLogBuilder createdAt(final Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcCatalogSyncLog build() {
            return new OndcCatalogSyncLog(this.id, this.outletId, this.syncType, this.itemsSynced, this.status, this.errorDetails, this.triggeredBy, this.createdAt);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcCatalogSyncLog.OndcCatalogSyncLogBuilder(id=" + this.id + ", outletId=" + this.outletId + ", syncType=" + this.syncType + ", itemsSynced=" + this.itemsSynced + ", status=" + this.status + ", errorDetails=" + this.errorDetails + ", triggeredBy=" + this.triggeredBy + ", createdAt=" + this.createdAt + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcCatalogSyncLog.OndcCatalogSyncLogBuilder builder() {
        return new OndcCatalogSyncLog.OndcCatalogSyncLogBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getOutletId() {
        return this.outletId;
    }

    @java.lang.SuppressWarnings("all")
    public String getSyncType() {
        return this.syncType;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getItemsSynced() {
        return this.itemsSynced;
    }

    @java.lang.SuppressWarnings("all")
    public String getStatus() {
        return this.status;
    }

    @java.lang.SuppressWarnings("all")
    public String getErrorDetails() {
        return this.errorDetails;
    }

    @java.lang.SuppressWarnings("all")
    public String getTriggeredBy() {
        return this.triggeredBy;
    }

    @java.lang.SuppressWarnings("all")
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final UUID id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setOutletId(final UUID outletId) {
        this.outletId = outletId;
    }

    @java.lang.SuppressWarnings("all")
    public void setSyncType(final String syncType) {
        this.syncType = syncType;
    }

    @java.lang.SuppressWarnings("all")
    public void setItemsSynced(final Integer itemsSynced) {
        this.itemsSynced = itemsSynced;
    }

    @java.lang.SuppressWarnings("all")
    public void setStatus(final String status) {
        this.status = status;
    }

    @java.lang.SuppressWarnings("all")
    public void setErrorDetails(final String errorDetails) {
        this.errorDetails = errorDetails;
    }

    @java.lang.SuppressWarnings("all")
    public void setTriggeredBy(final String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    @java.lang.SuppressWarnings("all")
    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public OndcCatalogSyncLog() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcCatalogSyncLog(final UUID id, final UUID outletId, final String syncType, final Integer itemsSynced, final String status, final String errorDetails, final String triggeredBy, final Instant createdAt) {
        this.id = id;
        this.outletId = outletId;
        this.syncType = syncType;
        this.itemsSynced = itemsSynced;
        this.status = status;
        this.errorDetails = errorDetails;
        this.triggeredBy = triggeredBy;
        this.createdAt = createdAt;
    }
}

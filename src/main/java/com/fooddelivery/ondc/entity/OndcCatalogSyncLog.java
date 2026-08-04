package com.fooddelivery.ondc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit trail of catalog syndication events and incremental delta pushes.
 */
@Entity
@Table(name = "ondc_catalog_sync_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}

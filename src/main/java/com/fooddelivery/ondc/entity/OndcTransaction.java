package com.fooddelivery.ondc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Tracks every ONDC transaction lifecycle (message_id, transaction_id, flow, state).
 * Used for log validation, certification, and audit.
 */
@Entity
@Table(name = "ondc_transactions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"transaction_id", "message_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OndcTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "flow_id")
    private String flowId;

    @Column(name = "bap_id")
    private String bapId;

    @Column(name = "bpp_id")
    private String bppId;

    @Column(name = "state")
    private String state;

    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "internal_order_id")
    private UUID internalOrderId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Version
    private Long version;
}

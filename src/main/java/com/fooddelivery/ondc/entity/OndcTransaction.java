package com.fooddelivery.ondc.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Tracks every ONDC transaction lifecycle (message_id, transaction_id, flow, state).
 * Used for log validation, certification, and audit.
 */
@Entity
@Table(name = "ondc_transactions", uniqueConstraints = @UniqueConstraint(columnNames = {"transaction_id", "message_id"}))
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


    @java.lang.SuppressWarnings("all")
    public static class OndcTransactionBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private String transactionId;
        @java.lang.SuppressWarnings("all")
        private String messageId;
        @java.lang.SuppressWarnings("all")
        private String action;
        @java.lang.SuppressWarnings("all")
        private String flowId;
        @java.lang.SuppressWarnings("all")
        private String bapId;
        @java.lang.SuppressWarnings("all")
        private String bppId;
        @java.lang.SuppressWarnings("all")
        private String state;
        @java.lang.SuppressWarnings("all")
        private String requestPayload;
        @java.lang.SuppressWarnings("all")
        private String responsePayload;
        @java.lang.SuppressWarnings("all")
        private String errorMessage;
        @java.lang.SuppressWarnings("all")
        private UUID internalOrderId;
        @java.lang.SuppressWarnings("all")
        private Instant createdAt;
        @java.lang.SuppressWarnings("all")
        private Long version;

        @java.lang.SuppressWarnings("all")
        OndcTransactionBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder transactionId(final String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder messageId(final String messageId) {
            this.messageId = messageId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder action(final String action) {
            this.action = action;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder flowId(final String flowId) {
            this.flowId = flowId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder bapId(final String bapId) {
            this.bapId = bapId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder bppId(final String bppId) {
            this.bppId = bppId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder state(final String state) {
            this.state = state;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder requestPayload(final String requestPayload) {
            this.requestPayload = requestPayload;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder responsePayload(final String responsePayload) {
            this.responsePayload = responsePayload;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder errorMessage(final String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder internalOrderId(final UUID internalOrderId) {
            this.internalOrderId = internalOrderId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder createdAt(final Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public OndcTransaction.OndcTransactionBuilder version(final Long version) {
            this.version = version;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public OndcTransaction build() {
            return new OndcTransaction(this.id, this.transactionId, this.messageId, this.action, this.flowId, this.bapId, this.bppId, this.state, this.requestPayload, this.responsePayload, this.errorMessage, this.internalOrderId, this.createdAt, this.version);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "OndcTransaction.OndcTransactionBuilder(id=" + this.id + ", transactionId=" + this.transactionId + ", messageId=" + this.messageId + ", action=" + this.action + ", flowId=" + this.flowId + ", bapId=" + this.bapId + ", bppId=" + this.bppId + ", state=" + this.state + ", requestPayload=" + this.requestPayload + ", responsePayload=" + this.responsePayload + ", errorMessage=" + this.errorMessage + ", internalOrderId=" + this.internalOrderId + ", createdAt=" + this.createdAt + ", version=" + this.version + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static OndcTransaction.OndcTransactionBuilder builder() {
        return new OndcTransaction.OndcTransactionBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
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
    public String getAction() {
        return this.action;
    }

    @java.lang.SuppressWarnings("all")
    public String getFlowId() {
        return this.flowId;
    }

    @java.lang.SuppressWarnings("all")
    public String getBapId() {
        return this.bapId;
    }

    @java.lang.SuppressWarnings("all")
    public String getBppId() {
        return this.bppId;
    }

    @java.lang.SuppressWarnings("all")
    public String getState() {
        return this.state;
    }

    @java.lang.SuppressWarnings("all")
    public String getRequestPayload() {
        return this.requestPayload;
    }

    @java.lang.SuppressWarnings("all")
    public String getResponsePayload() {
        return this.responsePayload;
    }

    @java.lang.SuppressWarnings("all")
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getInternalOrderId() {
        return this.internalOrderId;
    }

    @java.lang.SuppressWarnings("all")
    public Instant getCreatedAt() {
        return this.createdAt;
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
    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }

    @java.lang.SuppressWarnings("all")
    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    @java.lang.SuppressWarnings("all")
    public void setAction(final String action) {
        this.action = action;
    }

    @java.lang.SuppressWarnings("all")
    public void setFlowId(final String flowId) {
        this.flowId = flowId;
    }

    @java.lang.SuppressWarnings("all")
    public void setBapId(final String bapId) {
        this.bapId = bapId;
    }

    @java.lang.SuppressWarnings("all")
    public void setBppId(final String bppId) {
        this.bppId = bppId;
    }

    @java.lang.SuppressWarnings("all")
    public void setState(final String state) {
        this.state = state;
    }

    @java.lang.SuppressWarnings("all")
    public void setRequestPayload(final String requestPayload) {
        this.requestPayload = requestPayload;
    }

    @java.lang.SuppressWarnings("all")
    public void setResponsePayload(final String responsePayload) {
        this.responsePayload = responsePayload;
    }

    @java.lang.SuppressWarnings("all")
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @java.lang.SuppressWarnings("all")
    public void setInternalOrderId(final UUID internalOrderId) {
        this.internalOrderId = internalOrderId;
    }

    @java.lang.SuppressWarnings("all")
    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setVersion(final Long version) {
        this.version = version;
    }

    @java.lang.SuppressWarnings("all")
    public OndcTransaction() {
    }

    @java.lang.SuppressWarnings("all")
    public OndcTransaction(final UUID id, final String transactionId, final String messageId, final String action, final String flowId, final String bapId, final String bppId, final String state, final String requestPayload, final String responsePayload, final String errorMessage, final UUID internalOrderId, final Instant createdAt, final Long version) {
        this.id = id;
        this.transactionId = transactionId;
        this.messageId = messageId;
        this.action = action;
        this.flowId = flowId;
        this.bapId = bapId;
        this.bppId = bppId;
        this.state = state;
        this.requestPayload = requestPayload;
        this.responsePayload = responsePayload;
        this.errorMessage = errorMessage;
        this.internalOrderId = internalOrderId;
        this.createdAt = createdAt;
        this.version = version;
    }
}

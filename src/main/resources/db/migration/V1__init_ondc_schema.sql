-- ONDC Integration Service Schema
-- All primary keys use RAW(16) for UUIDs in Oracle
-- Removed PostgreSQL-specific IF NOT EXISTS and JSON/TEXT types.

CREATE TABLE ondc_network_participants (
    id RAW(16) PRIMARY KEY,
    subscriber_id VARCHAR2(255) NOT NULL UNIQUE,
    subscriber_url VARCHAR2(512) NOT NULL,
    domain VARCHAR2(100) NOT NULL,
    city VARCHAR2(100) NOT NULL,
    country VARCHAR2(10) NOT NULL,
    type VARCHAR2(20) NOT NULL,
    msn NUMBER(1) DEFAULT 0 NOT NULL,
    status VARCHAR2(20) NOT NULL,
    signing_public_key VARCHAR2(1024),
    encryption_public_key VARCHAR2(1024),
    valid_from TIMESTAMP,
    valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE ondc_transactions (
    id RAW(16) PRIMARY KEY,
    transaction_id VARCHAR2(255) NOT NULL,
    message_id VARCHAR2(255) NOT NULL,
    action VARCHAR2(50) NOT NULL,
    flow_id VARCHAR2(255),
    bap_id VARCHAR2(255),
    bpp_id VARCHAR2(255),
    state VARCHAR2(50),
    request_payload CLOB,
    response_payload CLOB,
    error_message VARCHAR2(1024),
    internal_order_id RAW(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT uq_transaction_message UNIQUE (transaction_id, message_id)
);

CREATE INDEX idx_ondc_txn_transaction_id ON ondc_transactions(transaction_id);
CREATE INDEX idx_ondc_txn_action ON ondc_transactions(transaction_id, action);

CREATE TABLE ondc_settlements (
    id RAW(16) PRIMARY KEY,
    ondc_transaction_id VARCHAR2(255) NOT NULL,
    internal_order_id RAW(16),
    settlement_type VARCHAR2(50) NOT NULL,
    collector_id VARCHAR2(255),
    receiver_id VARCHAR2(255),
    amount NUMBER(12, 2) NOT NULL,
    currency VARCHAR2(10) NOT NULL,
    status VARCHAR2(50) NOT NULL,
    settlement_reference VARCHAR2(255),
    error_details VARCHAR2(1024),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version NUMBER(19) DEFAULT 0 NOT NULL,
    CONSTRAINT uq_settlement_transaction UNIQUE (ondc_transaction_id, settlement_type)
);

CREATE INDEX idx_settlement_txn_id ON ondc_settlements(ondc_transaction_id);

CREATE TABLE ondc_catalog_sync_logs (
    id RAW(16) PRIMARY KEY,
    sync_id VARCHAR2(255) NOT NULL UNIQUE,
    city VARCHAR2(100) NOT NULL,
    status VARCHAR2(50) NOT NULL,
    items_synced NUMBER(10) DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

-- Outbox events table for the Transactional Outbox Pattern (CommonLibrary OutboxEventPoller)
CREATE TABLE outbox_events (
    id RAW(16) PRIMARY KEY,
    aggregate_type VARCHAR2(255) NOT NULL,
    aggregate_id VARCHAR2(255) NOT NULL,
    event_type VARCHAR2(255) NOT NULL,
    payload CLOB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    status VARCHAR2(20) DEFAULT 'PENDING' NOT NULL
);

CREATE INDEX idx_outbox_pending ON outbox_events(status, created_at);

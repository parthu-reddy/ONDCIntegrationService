-- ONDC Integration Service Schema

CREATE TABLE ondc_network_participants (
    id UUID PRIMARY KEY,
    subscriber_id VARCHAR(255) NOT NULL UNIQUE,
    subscriber_url VARCHAR(512) NOT NULL,
    domain VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL,
    msn INTEGER DEFAULT 0 NOT NULL,
    status VARCHAR(20) NOT NULL,
    signing_public_key VARCHAR(1024),
    encryption_public_key VARCHAR(1024),
    valid_from TIMESTAMP,
    valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE ondc_transactions (
    id UUID PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    message_id VARCHAR(255) NOT NULL,
    action VARCHAR(50) NOT NULL,
    flow_id VARCHAR(255),
    bap_id VARCHAR(255),
    bpp_id VARCHAR(255),
    state VARCHAR(50),
    request_payload TEXT,
    response_payload TEXT,
    error_message VARCHAR(1024),
    internal_order_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,
    CONSTRAINT uq_transaction_message UNIQUE (transaction_id, message_id)
);

CREATE INDEX idx_ondc_txn_transaction_id ON ondc_transactions(transaction_id);
CREATE INDEX idx_ondc_txn_action ON ondc_transactions(transaction_id, action);

CREATE TABLE ondc_settlements (
    id UUID PRIMARY KEY,
    ondc_transaction_id VARCHAR(255) NOT NULL,
    internal_order_id UUID,
    settlement_type VARCHAR(50) NOT NULL,
    collector_id VARCHAR(255),
    receiver_id VARCHAR(255),
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    settlement_reference VARCHAR(255),
    error_details VARCHAR(1024),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,
    CONSTRAINT uq_settlement_transaction UNIQUE (ondc_transaction_id, settlement_type)
);

CREATE INDEX idx_settlement_txn_id ON ondc_settlements(ondc_transaction_id);

CREATE TABLE ondc_catalog_sync_logs (
    id UUID PRIMARY KEY,
    sync_id VARCHAR(255) NOT NULL UNIQUE,
    city VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    items_synced INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

-- Outbox events table for the Transactional Outbox Pattern (CommonLibrary OutboxEventPoller)
CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL
);

CREATE INDEX idx_outbox_pending ON outbox_events(status, created_at);

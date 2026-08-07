CREATE TABLE ondc_catalog_sync_log (
    id UUID PRIMARY KEY,
    outlet_id UUID NOT NULL,
    sync_type VARCHAR(255) NOT NULL,
    items_synced INTEGER,
    status VARCHAR(255) NOT NULL,
    error_details VARCHAR(255),
    triggered_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ondc_catalog_sync_log_outlet_id ON ondc_catalog_sync_log(outlet_id);

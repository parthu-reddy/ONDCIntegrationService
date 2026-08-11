ALTER TABLE outbox_events ADD COLUMN error_message VARCHAR(1024);
ALTER TABLE outbox_events ADD COLUMN retry_count INTEGER DEFAULT 0 NOT NULL;
ALTER TABLE outbox_events RENAME COLUMN event_type TO type;

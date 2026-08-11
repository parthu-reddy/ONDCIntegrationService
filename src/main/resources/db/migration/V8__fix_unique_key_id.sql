-- Update any null rows to a placeholder (if any) then apply NOT NULL constraint
UPDATE ondc_network_participants SET unique_key_id = subscriber_id WHERE unique_key_id IS NULL;
ALTER TABLE ondc_network_participants ALTER COLUMN unique_key_id SET NOT NULL;

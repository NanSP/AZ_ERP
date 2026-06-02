ALTER TABLE platform.system_users
    ADD COLUMN IF NOT EXISTS password_change_required BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE platform.system_users
    ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMP;

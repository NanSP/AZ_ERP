CREATE TABLE platform.template_registry (
    id BIGSERIAL PRIMARY KEY,
    database_name VARCHAR(100) NOT NULL UNIQUE,
    current_version VARCHAR(20),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    lock_active BOOLEAN NOT NULL DEFAULT FALSE,
    last_migrated_at TIMESTAMP,
    last_validated_at TIMESTAMP,
    last_cloned_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_template_registry_status ON platform.template_registry(status);

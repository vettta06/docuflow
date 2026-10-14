-- Initial schema placeholder.
-- Real tables will be added in subsequent migrations.
CREATE TABLE IF NOT EXISTS _healthcheck (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

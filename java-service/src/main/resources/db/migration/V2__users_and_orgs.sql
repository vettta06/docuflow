-- Organizations
CREATE TABLE organizations (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    inn          VARCHAR(12)  NOT NULL UNIQUE,
    ogrn         VARCHAR(15),
    address      TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Users
CREATE TABLE users (
    id               BIGSERIAL PRIMARY KEY,
    role             VARCHAR(20)  NOT NULL,
    full_name        VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    phone            VARCHAR(20),
    organization_id  BIGINT REFERENCES organizations(id),
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_users_role CHECK (role IN ('CLIENT', 'ACCOUNTANT', 'MANAGER'))
);

CREATE INDEX idx_users_role            ON users(role);
CREATE INDEX idx_users_organization_id ON users(organization_id);

CREATE TABLE accountant_clients (
    id               BIGSERIAL PRIMARY KEY,
    accountant_id    BIGINT    NOT NULL REFERENCES users(id),
    organization_id  BIGINT    NOT NULL REFERENCES organizations(id),
    assigned_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_accountant_org UNIQUE (accountant_id, organization_id)
);

CREATE INDEX idx_accountant_clients_accountant  ON accountant_clients(accountant_id);
CREATE INDEX idx_accountant_clients_organization ON accountant_clients(organization_id);

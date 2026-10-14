CREATE TABLE tasks (
    id             BIGSERIAL PRIMARY KEY,
    operation_id   BIGINT       REFERENCES operations(id) ON DELETE CASCADE,
    document_id    BIGINT       REFERENCES documents(id)  ON DELETE SET NULL,
    created_by     BIGINT       NOT NULL REFERENCES users(id),
    assigned_to    BIGINT       REFERENCES users(id),
    document_type  VARCHAR(50),
    comment        TEXT,
    status         VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    fulfilled_at   TIMESTAMP,
    CONSTRAINT chk_tasks_status
        CHECK (status IN ('OPEN', 'IN_PROGRESS', 'FULFILLED', 'CANCELLED')),
    CONSTRAINT chk_tasks_document_type
        CHECK (document_type IS NULL OR document_type IN (
            'INVOICE', 'ACT', 'CONTRACT', 'PAYMENT_CONFIRMATION',
            'PAYROLL', 'RECEIPT', 'OTHER'
        ))
);

CREATE INDEX idx_tasks_operation   ON tasks(operation_id);
CREATE INDEX idx_tasks_document    ON tasks(document_id);
CREATE INDEX idx_tasks_created_by  ON tasks(created_by);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_tasks_status      ON tasks(status);

CREATE TABLE comments (
    id            BIGSERIAL PRIMARY KEY,
    author_id     BIGINT    NOT NULL REFERENCES users(id),
    document_id   BIGINT    REFERENCES documents(id)  ON DELETE CASCADE,
    operation_id  BIGINT    REFERENCES operations(id) ON DELETE CASCADE,
    text          TEXT      NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_comments_target
        CHECK (
            (document_id IS NOT NULL AND operation_id IS NULL) OR
            (document_id IS NULL AND operation_id IS NOT NULL)
        )
);

CREATE INDEX idx_comments_author    ON comments(author_id);
CREATE INDEX idx_comments_document  ON comments(document_id);
CREATE INDEX idx_comments_operation ON comments(operation_id);

CREATE TABLE audit_log (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       REFERENCES users(id),
    entity_type  VARCHAR(50)  NOT NULL,
    entity_id    BIGINT       NOT NULL,
    action       VARCHAR(50)  NOT NULL,
    old_value    TEXT,
    new_value    TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_log_user        ON audit_log(user_id);
CREATE INDEX idx_audit_log_entity      ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_created_at  ON audit_log(created_at);

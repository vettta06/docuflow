CREATE TABLE documents (
    id                BIGSERIAL PRIMARY KEY,
    organization_id   BIGINT       NOT NULL REFERENCES organizations(id),
    uploaded_by       BIGINT       REFERENCES users(id),
    document_type     VARCHAR(50)  NOT NULL,
    status            VARCHAR(30)  NOT NULL DEFAULT 'UPLOADED',
    file_path         VARCHAR(500) NOT NULL,
    file_name         VARCHAR(255) NOT NULL,
    file_size         BIGINT,
    mime_type         VARCHAR(100),
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_documents_status
        CHECK (status IN (
            'UPLOADED',
            'PROCESSING',
            'RECOGNIZED',
            'NEEDS_REVIEW',
            'CONFIRMED',
            'REJECTED',
            'WAITING_ADDITIONAL'
        )),
    CONSTRAINT chk_documents_type
        CHECK (document_type IN (
            'INVOICE',
            'ACT',
            'CONTRACT',
            'PAYMENT_CONFIRMATION',
            'PAYROLL',
            'RECEIPT',
            'OTHER'
        ))
);

CREATE INDEX idx_documents_organization ON documents(organization_id);
CREATE INDEX idx_documents_uploaded_by  ON documents(uploaded_by);
CREATE INDEX idx_documents_status       ON documents(status);
CREATE INDEX idx_documents_type         ON documents(document_type);
CREATE INDEX idx_documents_created_at   ON documents(created_at);

CREATE TABLE document_data (
    id            BIGSERIAL PRIMARY KEY,
    document_id   BIGINT       NOT NULL UNIQUE REFERENCES documents(id) ON DELETE CASCADE,
    number        VARCHAR(100),
    date          DATE,
    seller_inn    VARCHAR(12),
    seller_name   VARCHAR(255),
    buyer_inn     VARCHAR(12),
    buyer_name    VARCHAR(255),
    amount        NUMERIC(15, 2),
    vat           NUMERIC(15, 2),
    raw_ocr       TEXT,
    confidence    NUMERIC(5, 4),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_document_data_number     ON document_data(number);
CREATE INDEX idx_document_data_date       ON document_data(date);
CREATE INDEX idx_document_data_seller_inn ON document_data(seller_inn);
CREATE INDEX idx_document_data_buyer_inn  ON document_data(buyer_inn);

CREATE TABLE operation_documents (
    id            BIGSERIAL PRIMARY KEY,
    operation_id  BIGINT    NOT NULL REFERENCES operations(id) ON DELETE CASCADE,
    document_id   BIGINT    NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    added_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_operation_document UNIQUE (operation_id, document_id)
);

CREATE INDEX idx_operation_documents_operation ON operation_documents(operation_id);
CREATE INDEX idx_operation_documents_document  ON operation_documents(document_id);

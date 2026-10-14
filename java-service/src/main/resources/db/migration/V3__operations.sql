CREATE TABLE operation_types (
    id           BIGSERIAL PRIMARY KEY,
    code         VARCHAR(50)  NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL,
    description  TEXT
);

CREATE TABLE required_documents (
    id                 BIGSERIAL PRIMARY KEY,
    operation_type_id  BIGINT       NOT NULL REFERENCES operation_types(id),
    document_type      VARCHAR(50)  NOT NULL,
    is_mandatory       BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_required_doc UNIQUE (operation_type_id, document_type)
);

CREATE INDEX idx_required_documents_op_type ON required_documents(operation_type_id);

CREATE TABLE operations (
    id                   BIGSERIAL PRIMARY KEY,
    organization_id      BIGINT         NOT NULL REFERENCES organizations(id),
    accountant_id        BIGINT         REFERENCES users(id),
    type_id              BIGINT         NOT NULL REFERENCES operation_types(id),
    title                VARCHAR(255)   NOT NULL,
    total_amount         NUMERIC(15, 2),
    operation_date       DATE,
    status               VARCHAR(20)    NOT NULL DEFAULT 'NEW',
    completeness_status  VARCHAR(20)    NOT NULL DEFAULT 'INCOMPLETE',
    created_by           BIGINT         REFERENCES users(id),
    created_at           TIMESTAMP      NOT NULL DEFAULT NOW(),
    accepted_at          TIMESTAMP,
    CONSTRAINT chk_operations_status
        CHECK (status IN ('NEW', 'IN_PROGRESS', 'WAITING_CLIENT', 'READY', 'ACCEPTED')),
    CONSTRAINT chk_operations_completeness
        CHECK (completeness_status IN ('INCOMPLETE', 'COMPLETE'))
);

CREATE INDEX idx_operations_organization ON operations(organization_id);
CREATE INDEX idx_operations_accountant   ON operations(accountant_id);
CREATE INDEX idx_operations_status       ON operations(status);
CREATE INDEX idx_operations_type         ON operations(type_id);
CREATE INDEX idx_operations_date         ON operations(operation_date);

INSERT INTO operation_types (code, name, description) VALUES
    ('EQUIPMENT_PURCHASE', 'Покупка оборудования',    'Приобретение основных средств и оборудования'),
    ('GOODS_PURCHASE',     'Покупка товаров и услуг', 'Закупка ТМЦ и услуг у поставщиков'),
    ('SALE',               'Реализация',              'Продажа товаров, работ, услуг'),
    ('SALARY',             'Зарплата',                'Расчёт заработной платы сотрудникам'),
    ('RENT',               'Аренда',                  'Аренда помещений, оборудования');

INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'INVOICE', TRUE FROM operation_types WHERE code = 'EQUIPMENT_PURCHASE';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'CONTRACT', TRUE FROM operation_types WHERE code = 'EQUIPMENT_PURCHASE';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'ACT', TRUE FROM operation_types WHERE code = 'EQUIPMENT_PURCHASE';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'PAYMENT_CONFIRMATION', TRUE FROM operation_types WHERE code = 'EQUIPMENT_PURCHASE';

INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'INVOICE', TRUE FROM operation_types WHERE code = 'GOODS_PURCHASE';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'ACT', TRUE FROM operation_types WHERE code = 'GOODS_PURCHASE';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'PAYMENT_CONFIRMATION', TRUE FROM operation_types WHERE code = 'GOODS_PURCHASE';

INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'INVOICE', TRUE FROM operation_types WHERE code = 'SALE';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'ACT', TRUE FROM operation_types WHERE code = 'SALE';

INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'PAYROLL', TRUE FROM operation_types WHERE code = 'SALARY';

INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'INVOICE', TRUE FROM operation_types WHERE code = 'RENT';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'CONTRACT', TRUE FROM operation_types WHERE code = 'RENT';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'ACT', TRUE FROM operation_types WHERE code = 'RENT';
INSERT INTO required_documents (operation_type_id, document_type, is_mandatory)
SELECT id, 'PAYMENT_CONFIRMATION', TRUE FROM operation_types WHERE code = 'RENT';
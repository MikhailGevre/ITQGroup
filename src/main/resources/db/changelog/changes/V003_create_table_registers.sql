CREATE TABLE registers (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL UNIQUE,
    approved_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_registers_document
        FOREIGN KEY (document_id)
        REFERENCES documents (id)
        ON DELETE CASCADE
);
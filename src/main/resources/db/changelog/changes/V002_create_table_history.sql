CREATE TABLE histories
(
    id         BIGSERIAL PRIMARY KEY,
    author     VARCHAR(64)  NOT NULL,
    comment    VARCHAR(256) NOT NULL,
    action     SMALLINT     NOT NULL,
    document_id BIGINT  NOT NULL,
    created_at TIMESTAMP    NOT NULL,

    CONSTRAINT fk_document_history
        FOREIGN KEY (document_id)
        REFERENCES documents (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_histories_document_id
    ON histories(document_id);
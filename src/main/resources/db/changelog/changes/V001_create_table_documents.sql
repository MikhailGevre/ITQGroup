CREATE TABLE documents
(
    id         BIGSERIAL PRIMARY KEY,
    author VARCHAR(64) NOT NULL,
    title      VARCHAR(256) NOT NULL,
    status     SMALLINT     NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);
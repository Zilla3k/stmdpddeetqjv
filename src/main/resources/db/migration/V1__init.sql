CREATE TABLE migration_check (
     id BIGSERIAL PRIMARY KEY,
     created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- =========================================================
-- V6: Create capabilities table
-- =========================================================
-- Stores fine-grained permissions that may be assigned to
-- roles for authorization purposes.
-- =========================================================

CREATE TABLE capabilities
(
    id BIGSERIAL PRIMARY KEY,

    -- Public identifier used by the API instead of the internal id.
    uuid UUID NOT NULL UNIQUE,

    -- Unique capability name, for example MOVIE_CREATE or USER_MANAGE.
    name VARCHAR(100) NOT NULL UNIQUE,

    -- Optional description of the capability.
    description VARCHAR(255),

    -- Auditing and soft delete fields inherited from AbstractEntity.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ
);

-- Speeds up capability lookups during authorization.
CREATE INDEX idx_capabilities_name
    ON capabilities (name);
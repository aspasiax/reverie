-- =========================================================
-- V4: Create roles table
-- =========================================================
-- Stores security roles used for authorization.
-- Roles define broad access levels, such as USER and ADMIN.
-- =========================================================

CREATE TABLE roles
(
    id BIGSERIAL PRIMARY KEY,

    -- Public identifier used by the API instead of the internal id.
    uuid UUID NOT NULL UNIQUE,

    -- Unique role name, for example USER or ADMIN.
    name VARCHAR(50) NOT NULL UNIQUE,

    -- Optional description of the role.
    description VARCHAR(255),

    -- Auditing and soft delete fields inherited from AbstractEntity.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ
);

-- Speeds up role lookups during authentication and authorization.
CREATE INDEX idx_roles_name
    ON roles (name);
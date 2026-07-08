-- =========================================================
-- V5: Create users table
-- =========================================================
-- Stores registered Reverie users.
-- Users are connected to roles for authorization purposes.
-- =========================================================

CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,

    -- Public identifier used by the API instead of the internal id.
    uuid UUID NOT NULL UNIQUE,

    -- Login and profile identity.
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    -- Optional profile information.
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    bio VARCHAR(500),
    profile_image_url VARCHAR(1024),

    -- Account status.
    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    -- User role.
    role_id BIGINT NOT NULL,

    -- Auditing and soft delete fields inherited from AbstractEntity.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
);

-- Speeds up user lookups during login.
CREATE INDEX idx_users_username
    ON users (username);

-- Speeds up email-based user lookups.
CREATE INDEX idx_users_email
    ON users (email);

-- Speeds up role-based filtering.
CREATE INDEX idx_users_role_id
    ON users (role_id);
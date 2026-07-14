-- =========================================================
-- V5: Create users table
-- =========================================================
-- Stores registered Reverie users.
-- Usernames and email addresses are unique regardless of
-- uppercase or lowercase characters.
-- Users are connected to roles for authorization purposes.
-- =========================================================

CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,

    -- Public identifier used by the API instead of the internal id.
    uuid UUID NOT NULL UNIQUE,

    -- Authentication and public identity.
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(150) NOT NULL,

    -- Optional profile information.
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

-- Enforces case-insensitive username uniqueness.
CREATE UNIQUE INDEX uq_users_username_lower
    ON users (LOWER(username));

-- Enforces case-insensitive email uniqueness.
CREATE UNIQUE INDEX uq_users_email_lower
    ON users (LOWER(email));

-- Supports role-based user filtering.
CREATE INDEX idx_users_role_id
    ON users (role_id);
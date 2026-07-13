-- =========================================================
-- V8__seed_roles_table.sql
-- =========================================================
-- Inserts the default system roles required by the application.
-- Roles:
-- USER  : Standard application user.
-- ADMIN : Administrator with elevated privileges.
-- =========================================================

INSERT INTO roles (
    uuid,
    name,
    description,
    created_at,
    updated_at,
    deleted
)
VALUES
    (
        gen_random_uuid(),
        'USER',
        'Default user role',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'ADMIN',
        'Administrator role',
        NOW(),
        NOW(),
        FALSE
    );
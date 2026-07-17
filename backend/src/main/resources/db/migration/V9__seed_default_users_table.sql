-- =========================================================
-- V9: Seed default users
-- =========================================================
-- Inserts the default development and demonstration users.
--
-- These accounts allow immediate access to the application
-- without requiring manual registration.
--
-- Passwords are stored exclusively as BCrypt hashes.
--
-- Demo credentials:
--   admin  / admin123
--   alex   / user123
--   emma   / user123
--   daniel / user123
-- =========================================================

INSERT INTO users (
    uuid,
    username,
    email,
    password,
    display_name,
    enabled,
    role_id,
    created_at,
    updated_at,
    deleted,
    deleted_at
)
VALUES
    (
        gen_random_uuid(),
        'admin',
        'admin@reverie.com',
        '$2a$12$t4wuvZnKwZ9savS8VPrlQeiCoIjPDHYV96vZf8Or44rO60PdYk3DS',
        'Administrator',
        TRUE,
        (SELECT id FROM roles WHERE name = 'ADMIN'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    ),
    (
        gen_random_uuid(),
        'alex',
        'alex@reverie.com',
        '$2a$12$2FYfStVxaNl/reHBSwXj1OnIORv/W.3bT3IOFwYIJr4B7yHFM4.VG',
        'Alex',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    ),
    (
        gen_random_uuid(),
        'emma',
        'emma@reverie.com',
        '$2a$12$LeFM36GAyRowrHf.rcY13.OKnEPUOwEPvSUKrzEj.7qPHert74pAG',
        'Emma',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    ),
    (
        gen_random_uuid(),
        'daniel',
        'daniel@reverie.com',
        '$2a$12$/oQ.m7/pFo9MMHLMGIoTrudG.UpqD/JV4V6QmMSxik29N.lOJJE46',
        'Daniel',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    );

-- =========================================================
-- V901: Seed demo users
-- =========================================================
-- Inserts the demonstration accounts used during development
-- and for evaluating the application.
--
-- This migration lives in classpath:db/demo, which is loaded
-- only by the dev profile. The main migration chain therefore
-- never creates accounts with publicly known credentials.
--
-- Passwords are stored exclusively as BCrypt hashes and follow
-- the same complexity rules the registration endpoint enforces.
--
-- Demo credentials:
--   admin@reverie.com  / Admin123!
--   alex@reverie.com   / User123!
--   emma@reverie.com   / User123!
--   daniel@reverie.com / User123!
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
        '$2a$12$k8mAmDXVhY2V1m.2/ivWSencDpjJy1ulpqjKrNLSYfVcsD/gU.RFW',
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
        '$2a$12$.dzDDVAg2ctgu.hRUfRs.OGpcK66lgZM3sqEX287bXkAchWEUWQFO',
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
        '$2a$12$Go0RqTopGes6NBSF2.vlX.hxmmKCDMPSWhANicnSBsViAplew69jO',
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
        '$2a$12$NcWuieSHBdGJzlz8QXx7me5mmIuz8VhTpl57BIcTvEZSC602nXon6',
        'Daniel',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    );
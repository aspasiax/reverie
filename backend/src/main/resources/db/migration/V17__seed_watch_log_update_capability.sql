-- =========================================================
-- V17: Seed the watch log update capability
-- =========================================================
-- Watch logs could be created, read and deleted, but never
-- corrected. Recording the wrong date meant deleting the entry
-- and adding it again.
--
-- Both roles receive the capability: a watch log always belongs
-- to the user who created it, and ownership is verified in the
-- service layer.
-- =========================================================

INSERT INTO capabilities (
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
        'WATCH_LOG_UPDATE',
        'Allows updating watch-log entries owned by the authenticated user.',
        NOW(),
        NOW(),
        FALSE
    );


-- =========================================================
-- ADMIN
-- =========================================================

INSERT INTO role_capabilities (
    role_id,
    capability_id
)
SELECT
    r.id,
    c.id
FROM roles r
         CROSS JOIN capabilities c
WHERE r.name = 'ADMIN'
  AND c.name = 'WATCH_LOG_UPDATE';


-- =========================================================
-- USER
-- =========================================================

INSERT INTO role_capabilities (
    role_id,
    capability_id
)
SELECT
    r.id,
    c.id
FROM roles r
         CROSS JOIN capabilities c
WHERE r.name = 'USER'
  AND c.name = 'WATCH_LOG_UPDATE';
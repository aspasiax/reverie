-- =========================================================
-- V14: Seed watch log role capabilities
-- =========================================================
-- Assigns all watch-log capabilities to both predefined
-- roles.
--
-- ADMIN receives all watch-log permissions.
--
-- USER receives permissions required to manage their own
-- watch history.
-- =========================================================


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
  AND c.name IN (
                 'WATCH_LOG_READ',
                 'WATCH_LOG_CREATE',
                 'WATCH_LOG_DELETE'
    );


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
  AND c.name IN (
                 'WATCH_LOG_READ',
                 'WATCH_LOG_CREATE',
                 'WATCH_LOG_DELETE'
    );
-- =========================================================
-- V10: Seed role_capabilities table
-- =========================================================
-- Assigns the default capabilities to each system role.
--
-- USER role:
--   - Can create, update and delete their own reviews.
--
-- ADMIN role:
--   - Has access to all administration capabilities.
-- =========================================================

-- Assign review capabilities to the USER role.
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
                 'REVIEW_CREATE',
                 'REVIEW_UPDATE',
                 'REVIEW_DELETE'
    );

-- Assign all capabilities to the ADMIN role.
INSERT INTO role_capabilities (
    role_id,
    capability_id
)
SELECT
    r.id,
    c.id
FROM roles r
         CROSS JOIN capabilities c
WHERE r.name = 'ADMIN';
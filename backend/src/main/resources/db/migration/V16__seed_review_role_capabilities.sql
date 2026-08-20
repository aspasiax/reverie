-- ============================================================
-- V16: Seed review role capabilities
-- ============================================================
-- Assigns review capabilities to system roles.
-- ============================================================

INSERT INTO role_capabilities (role_id, capability_id)
SELECT r.id, c.id
FROM roles r
         CROSS JOIN capabilities c
WHERE r.name = 'ADMIN'
  AND c.name IN (
                 'REVIEW_READ',
                 'REVIEW_CREATE',
                 'REVIEW_UPDATE',
                 'REVIEW_DELETE'
    );

INSERT INTO role_capabilities (role_id, capability_id)
SELECT r.id, c.id
FROM roles r
         CROSS JOIN capabilities c
WHERE r.name = 'USER'
  AND c.name IN (
                 'REVIEW_READ',
                 'REVIEW_CREATE',
                 'REVIEW_UPDATE',
                 'REVIEW_DELETE'
    );
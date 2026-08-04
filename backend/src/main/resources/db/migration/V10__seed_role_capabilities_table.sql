-- =========================================================
-- V10: Seed role_capabilities table
-- =========================================================
-- Assigns capabilities to the predefined roles.
--
-- ADMIN receives all available capabilities.
--
-- USER receives read-only access to the currently
-- available resources.
--
-- Additional permissions will be assigned in future
-- migrations as new features are introduced.
-- =========================================================


-- =========================================================
-- ADMIN
-- =========================================================

INSERT INTO role_capabilities (role_id, capability_id)
SELECT
    r.id,
    c.id
FROM roles r
         CROSS JOIN capabilities c
WHERE r.name = 'ADMIN';


-- =========================================================
-- USER
-- =========================================================

INSERT INTO role_capabilities (role_id, capability_id)
SELECT
    r.id,
    c.id
FROM roles r
         JOIN capabilities c
              ON c.name IN (
                            'MOVIE_READ',
                            'GENRE_READ'
                  )
WHERE r.name = 'USER';
-- =========================================================
-- V21: Seed watchlist role capabilities
-- =========================================================
-- Assigns all watchlist capabilities to both predefined
-- roles.
--
-- A watchlist belongs to the account that owns it, so both
-- roles receive the same permissions: an administrator keeps
-- their own list like anyone else, and administering the
-- catalogue grants no view of what other people plan to
-- watch.
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
                 'WATCHLIST_READ',
                 'WATCHLIST_CREATE',
                 'WATCHLIST_DELETE'
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
                 'WATCHLIST_READ',
                 'WATCHLIST_CREATE',
                 'WATCHLIST_DELETE'
    );
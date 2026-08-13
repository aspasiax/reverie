-- =========================================================
-- V22: Seed the statistics capability
-- =========================================================
-- Guards the overview of the whole application: how many
-- accounts exist, how much of the catalogue is published,
-- how much activity there has been.
--
-- Only ADMIN receives it. Note that this is a different
-- question from the statistics of a single user, which any
-- signed in user may read: those summarise activity that is
-- already visible on the pages of the films it concerns,
-- while this one describes the installation itself.
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
        'STATISTICS_READ',
        'Allows reading the overview of the whole application.',
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
  AND c.name = 'STATISTICS_READ';
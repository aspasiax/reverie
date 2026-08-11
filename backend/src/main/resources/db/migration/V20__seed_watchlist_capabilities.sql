-- =========================================================
-- V20: Seed watchlist capabilities
-- =========================================================
-- Introduces the fine-grained permissions required for
-- viewing, adding and removing watchlist entries.
--
-- There is no update capability: an entry carries nothing
-- that can be changed. It either exists or it does not.
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
        'WATCHLIST_READ',
        'Allows viewing the authenticated user''s watchlist.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'WATCHLIST_CREATE',
        'Allows adding films to the authenticated user''s watchlist.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'WATCHLIST_DELETE',
        'Allows removing films from the authenticated user''s watchlist.',
        NOW(),
        NOW(),
        FALSE
    );
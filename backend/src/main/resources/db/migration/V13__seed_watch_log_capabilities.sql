-- =========================================================
-- V13: Seed watch log capabilities
-- =========================================================
-- Introduces the fine-grained permissions required for
-- viewing, creating and deleting watch-log entries.
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
        'WATCH_LOG_READ',
        'Allows viewing the authenticated user''s watch history.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'WATCH_LOG_CREATE',
        'Allows creating watch-log entries.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'WATCH_LOG_DELETE',
        'Allows deleting watch-log entries owned by the authenticated user.',
        NOW(),
        NOW(),
        FALSE
    );
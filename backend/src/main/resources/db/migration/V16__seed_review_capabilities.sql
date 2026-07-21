-- ============================================================
-- V16__seed_review_capabilities.sql
-- ============================================================
-- Seeds capabilities related to review management.
-- ============================================================

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
        'REVIEW_READ',
        'Allows viewing movie reviews.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'REVIEW_CREATE',
        'Allows creating movie reviews.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'REVIEW_UPDATE',
        'Allows updating reviews owned by the authenticated user.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'REVIEW_DELETE',
        'Allows deleting reviews owned by the authenticated user.',
        NOW(),
        NOW(),
        FALSE
    );
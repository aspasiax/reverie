-- =========================================================
-- V9: Seed capabilities table
-- =========================================================
-- Inserts the default fine-grained permissions used by the
-- application for role-based authorization.
--
-- The READ capabilities are intended primarily for protected
-- administration endpoints, while public movie and review
-- endpoints may remain accessible without authentication.
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
    -- Movie permissions
    (
        gen_random_uuid(),
        'MOVIE_CREATE',
        'Allows creating movies.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'MOVIE_READ',
        'Allows viewing movies in protected administration endpoints.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'MOVIE_UPDATE',
        'Allows updating movies.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'MOVIE_DELETE',
        'Allows deleting movies.',
        NOW(),
        NOW(),
        FALSE
    ),

    -- Review permissions
    (
        gen_random_uuid(),
        'REVIEW_CREATE',
        'Allows creating reviews and ratings.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'REVIEW_READ',
        'Allows viewing reviews in protected administration endpoints.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'REVIEW_UPDATE',
        'Allows updating reviews and ratings.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'REVIEW_DELETE',
        'Allows deleting reviews and ratings.',
        NOW(),
        NOW(),
        FALSE
    ),

    -- User permissions
    (
        gen_random_uuid(),
        'USER_CREATE',
        'Allows creating user accounts through administration endpoints.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'USER_READ',
        'Allows viewing registered users.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'USER_UPDATE',
        'Allows updating registered users.',
        NOW(),
        NOW(),
        FALSE
    ),
    (
        gen_random_uuid(),
        'USER_DELETE',
        'Allows deleting registered users.',
        NOW(),
        NOW(),
        FALSE
    );
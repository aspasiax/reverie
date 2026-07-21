-- ============================================================
-- V15__create_reviews_table.sql
-- ============================================================
-- Creates the reviews table.
--
-- Each review belongs to a user and a movie.
-- Users can optionally provide a rating, review text, or both.
-- A user can have only one active review per movie.
-- Soft delete is supported through the deleted flag.
-- ============================================================

CREATE TABLE reviews
(
    id BIGSERIAL PRIMARY KEY,

    uuid UUID NOT NULL UNIQUE,

    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,

    rating INTEGER,
    review_text TEXT,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_reviews_user
        FOREIGN KEY (user_id)
            REFERENCES users (id),

    CONSTRAINT fk_reviews_movie
        FOREIGN KEY (movie_id)
            REFERENCES movies (id),

    CONSTRAINT chk_reviews_rating
        CHECK (
            rating IS NULL
                OR rating BETWEEN 1 AND 10
            ),

    CONSTRAINT chk_reviews_content
        CHECK (
            rating IS NOT NULL
                OR NULLIF(BTRIM(review_text), '') IS NOT NULL
            )
);

CREATE UNIQUE INDEX uq_reviews_user_movie_active
    ON reviews (user_id, movie_id)
    WHERE deleted = FALSE;
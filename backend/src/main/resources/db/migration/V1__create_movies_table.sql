-- =========================================================
-- V1: Create movies table

-- Stores all movies available in Reverie.
-- Movies may be created manually by an admin or imported
-- from an external movie API such as TMDB.
-- =========================================================

CREATE TABLE movies
(
    id BIGSERIAL PRIMARY KEY,

    -- Public identifier used by the API instead of the internal id.
    uuid UUID NOT NULL UNIQUE,

    -- Basic movie information.
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255),
    overview TEXT,
    release_date DATE,
    runtime INTEGER,

    -- Media URLs.
    poster_url VARCHAR(1024),
    backdrop_url VARCHAR(1024),

    -- External TMDB identifier, used to avoid duplicate imports.
    tmdb_id BIGINT UNIQUE,

    -- Controls whether the movie is visible to users.
    published BOOLEAN NOT NULL DEFAULT FALSE,

    -- Auditing and soft delete fields inherited from AbstractEntity.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ
);

-- Speeds up lookups during TMDB imports and synchronization.
CREATE INDEX idx_movies_tmdb_id
    ON movies (tmdb_id);
-- =========================================================
-- V1: Create movies table
-- =========================================================
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

    runtime INTEGER
        CONSTRAINT chk_movies_runtime_positive
            CHECK (runtime IS NULL OR runtime > 0),

    original_language VARCHAR(10),

    -- Relative image paths, usually provided by TMDB.
    poster_path VARCHAR(1024),
    backdrop_path VARCHAR(1024),

    -- Optional external identifiers used for imports
    -- and external references.
    tmdb_id BIGINT UNIQUE
        CONSTRAINT chk_movies_tmdb_id_positive
            CHECK (tmdb_id IS NULL OR tmdb_id > 0),

    imdb_id VARCHAR(20) UNIQUE,

    -- Controls whether the movie is visible to users.
    published BOOLEAN NOT NULL DEFAULT FALSE,

    -- Auditing and soft delete fields inherited from AbstractEntity.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ
);
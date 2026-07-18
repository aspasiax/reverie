-- =========================================================
-- V12: Create watch_logs table
-- =========================================================
-- Stores individual movie viewing events recorded by users.
--
-- A user may create multiple watch logs for the same movie,
-- allowing Reverie to track rewatches as separate entries.
--
-- The watched_at field is optional because users may not
-- remember the exact viewing date.
-- =========================================================

CREATE TABLE watch_logs
(
    id BIGSERIAL PRIMARY KEY,

    -- Public identifier used by the API instead of the internal id.
    uuid UUID NOT NULL UNIQUE,

    -- User who recorded the viewing.
    user_id BIGINT NOT NULL,

    -- Movie associated with the viewing.
    movie_id BIGINT NOT NULL,

    -- Optional date on which the movie was watched.
    watched_at DATE,

    -- Auditing and soft-delete fields inherited from AbstractEntity.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_watch_logs_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_watch_logs_movie
        FOREIGN KEY (movie_id)
            REFERENCES movies (id)
            ON DELETE CASCADE
);

-- Supports retrieval of a user's complete viewing history.
CREATE INDEX idx_watch_logs_user_id
    ON watch_logs (user_id);

-- Supports retrieval of all viewings associated with a movie.
CREATE INDEX idx_watch_logs_movie_id
    ON watch_logs (movie_id);

-- Supports chronological sorting and filtering by watch date.
CREATE INDEX idx_watch_logs_watched_at
    ON watch_logs (watched_at);
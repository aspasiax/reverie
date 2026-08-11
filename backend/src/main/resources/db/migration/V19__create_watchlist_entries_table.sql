-- =========================================================
-- V19: Create watchlist_entries table
-- =========================================================
-- Stores the films a user intends to watch.
--
-- Unlike a watch log, which records something that happened
-- and may happen again, a watchlist entry records an
-- intention. An intention is held once: a user either wants
-- to see a film or already does, so a single active entry
-- per user and film is allowed.
--
-- The entry is removed when the film is logged as watched,
-- because at that point the intention has been fulfilled.
-- =========================================================

CREATE TABLE watchlist_entries
(
    id BIGSERIAL PRIMARY KEY,

    -- Public identifier used by the API instead of the internal id.
    uuid UUID NOT NULL UNIQUE,

    -- User who wants to watch the movie.
    user_id BIGINT NOT NULL,

    -- Movie the user intends to watch.
    movie_id BIGINT NOT NULL,

    -- Auditing and soft-delete fields inherited from AbstractEntity.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,

    CONSTRAINT fk_watchlist_entries_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_watchlist_entries_movie
        FOREIGN KEY (movie_id)
            REFERENCES movies (id)
            ON DELETE CASCADE
);

-- One active entry per user and movie. The condition matters: without it
-- a removed entry would reserve the pair forever, and the film could
-- never be added back.
--
-- This index also serves the two lookups the application performs, since
-- both begin with user_id: listing a user's watchlist, and finding the
-- entry for a user and a film. No further index is needed.
CREATE UNIQUE INDEX uq_watchlist_entries_user_movie_active
    ON watchlist_entries (user_id, movie_id)
    WHERE deleted = FALSE;
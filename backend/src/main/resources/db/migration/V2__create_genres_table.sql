-- =========================================================
-- V2: Create genres table
-- =========================================================
-- Stores movie genres used for browsing, filtering and
-- displaying genre badges in the Reverie frontend.
-- =========================================================

CREATE TABLE genres
(
    id BIGSERIAL PRIMARY KEY,

    -- Public identifier used by the API instead of the internal id.
    uuid UUID NOT NULL UNIQUE,

    -- Basic genre information.
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),

    -- Frontend presentation fields.
    icon VARCHAR(100),
    color VARCHAR(20),

    -- Auditing and soft delete fields inherited from AbstractEntity.
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ
);

-- Genre names are unique among active genres only. Comparison is
-- case-insensitive so that "Horror" and "horror" cannot coexist.
CREATE UNIQUE INDEX uq_genres_name_active
    ON genres (LOWER(name))
    WHERE deleted = FALSE;
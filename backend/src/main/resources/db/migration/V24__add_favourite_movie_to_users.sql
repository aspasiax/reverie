-- =========================================================
-- V24: Add the favourite movie to users
-- =========================================================
-- Lets a user name one film as their favourite, shown on
-- their profile for anyone who visits it.
--
-- Nullable, because naming a favourite is optional and
-- every account that already exists starts without one.
--
-- The database only guarantees that the film exists. The
-- rule that actually matters -- that the user has watched
-- it -- lives in the service, because it depends on the
-- watch logs of the account doing the naming and cannot be
-- written as a column constraint.
-- =========================================================

ALTER TABLE users
    ADD COLUMN favourite_movie_id BIGINT;

-- Films are soft-deleted, so this rarely fires. It is set rather than
-- cascading all the same: losing a film from the catalogue should cost a
-- user their favourite, never their account.
ALTER TABLE users
    ADD CONSTRAINT fk_users_favourite_movie
        FOREIGN KEY (favourite_movie_id)
            REFERENCES movies (id)
            ON DELETE SET NULL;

-- PostgreSQL indexes the key a foreign key points at, never the column
-- holding it. Without this, clearing the favourite of everyone who chose a
-- removed film would have to read every row in the table.
CREATE INDEX idx_users_favourite_movie
    ON users (favourite_movie_id);
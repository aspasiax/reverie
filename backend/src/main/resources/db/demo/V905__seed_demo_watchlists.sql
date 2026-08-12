-- =========================================================
-- V905: Seed demo watchlists
-- =========================================================
-- Gives each demo user a few films they intend to watch, so
-- the watchlist screen has something to show on first open.
--
-- The application removes an entry once the film is logged
-- as watched, so no user here has an entry for a film that
-- already appears in their viewing history. As with the
-- other demo files, this data could have been produced
-- through the API itself.
--
-- Alien is deliberately left off Alex's list: the smoke test
-- uses that pair to exercise adding and removing, and would
-- otherwise start from a film that is already there.
--
-- Rows are resolved through natural keys so the file never
-- depends on generated identifiers.
-- =========================================================

INSERT INTO watchlist_entries (
    uuid,
    user_id,
    movie_id,
    created_at,
    updated_at,
    deleted
)
SELECT
    gen_random_uuid(),
    u.id,
    m.id,
    entry.added_at,
    entry.added_at,
    FALSE
FROM (
         VALUES
             -- Alex
             ('alex@reverie.com',   'tt0245429', TIMESTAMPTZ '2026-03-04 20:10:00+02'),
             ('alex@reverie.com',   'tt5311514', TIMESTAMPTZ '2026-04-22 21:35:00+03'),
             ('alex@reverie.com',   'tt2278388', TIMESTAMPTZ '2026-06-02 18:45:00+03'),
             ('alex@reverie.com',   'tt0364569', TIMESTAMPTZ '2026-07-19 22:05:00+03'),

             -- Emma
             ('emma@reverie.com',   'tt1375666', TIMESTAMPTZ '2026-02-27 19:20:00+02'),
             ('emma@reverie.com',   'tt0120737', TIMESTAMPTZ '2026-05-11 20:55:00+03'),
             ('emma@reverie.com',   'tt6710474', TIMESTAMPTZ '2026-07-03 21:15:00+03'),

             -- Daniel
             ('daniel@reverie.com', 'tt6751668', TIMESTAMPTZ '2026-03-18 22:40:00+02'),
             ('daniel@reverie.com', 'tt0078748', TIMESTAMPTZ '2026-05-06 20:30:00+03'),
             ('daniel@reverie.com', 'tt2543164', TIMESTAMPTZ '2026-06-28 19:00:00+03')
     ) AS entry (email, imdb_id, added_at)
         JOIN users u ON u.email = entry.email
         JOIN movies m ON m.imdb_id = entry.imdb_id;
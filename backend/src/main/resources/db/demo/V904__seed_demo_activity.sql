-- =========================================================
-- V904: Seed demo watch logs and reviews
-- =========================================================
-- Inserts the viewing history and the reviews of the demo users.
--
-- The application enforces that a user may only review a film they
-- have already logged as watched, and that a user holds at most one
-- active review per film. This file respects both rules, so the demo
-- data could have been produced through the API itself:
--
--   * every review below has a matching watch log for the same
--     user and film
--   * no user reviews the same film twice
--
-- Two deliberate edge cases are included to exercise the schema:
--
--   * one watch log has no watch date, covering the case where a
--     user does not remember when they saw a film
--   * one review carries only a rating and another only written
--     text, both of which satisfy chk_reviews_content
--
-- Rows are resolved through natural keys so the file never depends
-- on generated identifiers.
-- =========================================================


-- =========================================================
-- Watch logs
-- =========================================================

INSERT INTO watch_logs (
    uuid,
    user_id,
    movie_id,
    watched_at,
    created_at,
    updated_at,
    deleted
)
SELECT
    gen_random_uuid(),
    u.id,
    m.id,
    entry.watched_at,
    entry.logged_at,
    entry.logged_at,
    FALSE
FROM (
         VALUES
             -- Alex
             ('alex@reverie.com',   'tt0111161', DATE '2026-01-12', TIMESTAMPTZ '2026-01-12 21:40:00+02'),
             ('alex@reverie.com',   'tt0816692', DATE '2026-02-03', TIMESTAMPTZ '2026-02-03 23:05:00+02'),
             ('alex@reverie.com',   'tt6751668', DATE '2026-02-20', TIMESTAMPTZ '2026-02-20 20:15:00+02'),
             ('alex@reverie.com',   'tt1375666', DATE '2026-03-15', TIMESTAMPTZ '2026-03-15 22:30:00+02'),
             ('alex@reverie.com',   'tt2543164', DATE '2026-04-02', TIMESTAMPTZ '2026-04-02 19:50:00+03'),
             ('alex@reverie.com',   'tt0133093', DATE '2026-04-28', TIMESTAMPTZ '2026-04-28 21:10:00+03'),
             ('alex@reverie.com',   'tt1856101', DATE '2026-05-17', TIMESTAMPTZ '2026-05-17 20:00:00+03'),
             ('alex@reverie.com',   'tt6710474', DATE '2026-06-09', TIMESTAMPTZ '2026-06-09 21:25:00+03'),
             -- A rewatch: the same film logged a second time.
             ('alex@reverie.com',   'tt0816692', DATE '2026-07-01', TIMESTAMPTZ '2026-07-01 22:45:00+03'),

             -- Emma
             ('emma@reverie.com',   'tt0068646', DATE '2026-01-05', TIMESTAMPTZ '2026-01-05 20:30:00+02'),
             ('emma@reverie.com',   'tt0245429', DATE '2026-01-29', TIMESTAMPTZ '2026-01-29 18:20:00+02'),
             ('emma@reverie.com',   'tt3783958', DATE '2026-02-14', TIMESTAMPTZ '2026-02-14 21:00:00+02'),
             ('emma@reverie.com',   'tt2582802', DATE '2026-03-08', TIMESTAMPTZ '2026-03-08 22:15:00+02'),
             ('emma@reverie.com',   'tt5052448', DATE '2026-04-11', TIMESTAMPTZ '2026-04-11 23:30:00+03'),
             ('emma@reverie.com',   'tt6751668', DATE '2026-05-02', TIMESTAMPTZ '2026-05-02 20:45:00+03'),
             ('emma@reverie.com',   'tt0109830', DATE '2026-05-30', TIMESTAMPTZ '2026-05-30 19:15:00+03'),
             ('emma@reverie.com',   'tt0364569', DATE '2026-06-21', TIMESTAMPTZ '2026-06-21 22:00:00+03'),
             ('emma@reverie.com',   'tt0245429', DATE '2026-07-14', TIMESTAMPTZ '2026-07-14 17:40:00+03'),

             -- Daniel
             ('daniel@reverie.com', 'tt0110912', DATE '2026-02-08', TIMESTAMPTZ '2026-02-08 21:20:00+02'),
             ('daniel@reverie.com', 'tt0099685', DATE '2026-03-01', TIMESTAMPTZ '2026-03-01 20:50:00+02'),
             ('daniel@reverie.com', 'tt0468569', DATE '2026-03-26', TIMESTAMPTZ '2026-03-26 22:10:00+02'),
             -- No watch date: the user does not remember when they saw it.
             ('daniel@reverie.com', 'tt0137523', NULL::date,        TIMESTAMPTZ '2026-04-19 18:00:00+03'),
             ('daniel@reverie.com', 'tt1392190', DATE '2026-05-24', TIMESTAMPTZ '2026-05-24 21:35:00+03'),
             ('daniel@reverie.com', 'tt0816692', DATE '2026-06-15', TIMESTAMPTZ '2026-06-15 20:25:00+03'),
             ('daniel@reverie.com', 'tt0133093', DATE '2026-07-08', TIMESTAMPTZ '2026-07-08 23:00:00+03')
     ) AS entry (email, imdb_id, watched_at, logged_at)
         JOIN users u ON u.email = entry.email
         JOIN movies m ON m.imdb_id = entry.imdb_id;


-- =========================================================
-- Reviews
-- =========================================================
-- Every pair below also appears in the watch logs above, which is
-- the precondition the review service enforces.
-- =========================================================

INSERT INTO reviews (
    uuid,
    user_id,
    movie_id,
    rating,
    review_text,
    created_at,
    updated_at,
    deleted
)
SELECT
    gen_random_uuid(),
    u.id,
    m.id,
    entry.rating,
    entry.review_text,
    entry.written_at,
    entry.written_at,
    FALSE
FROM (
         VALUES
             -- Alex
             ('alex@reverie.com', 'tt0111161', 9,
              'Slow in the best possible way. It earns every moment of its ending.',
              TIMESTAMPTZ '2026-01-13 09:15:00+02'),

             ('alex@reverie.com', 'tt0816692', 10,
              'The docking sequence alone justifies the whole film. Watched it twice and it held up.',
              TIMESTAMPTZ '2026-02-04 10:00:00+02'),

             ('alex@reverie.com', 'tt6751668', 9,
              'Switches genre halfway through without ever losing control of its own logic.',
              TIMESTAMPTZ '2026-02-21 12:30:00+02'),

             ('alex@reverie.com', 'tt2543164', 8,
              'Quiet science fiction that trusts the audience to keep up.',
              TIMESTAMPTZ '2026-04-03 08:45:00+03'),

             -- Rating only: no written text.
             ('alex@reverie.com', 'tt1856101', 8,
              NULL,
              TIMESTAMPTZ '2026-05-18 11:20:00+03'),

             ('alex@reverie.com', 'tt6710474', 9,
              'Chaotic for an hour, then suddenly about something very simple. It works.',
              TIMESTAMPTZ '2026-06-10 19:05:00+03'),

             -- Emma
             ('emma@reverie.com', 'tt0245429', 10,
              'Every frame feels hand made. I have seen it many times and still notice new details.',
              TIMESTAMPTZ '2026-01-30 13:10:00+02'),

             ('emma@reverie.com', 'tt3783958', 7,
              'Gorgeous to look at, though the ending is harder than the marketing suggests.',
              TIMESTAMPTZ '2026-02-15 16:40:00+02'),

             ('emma@reverie.com', 'tt2582802', 9,
              'Tense in a way I did not expect from a film about a music student.',
              TIMESTAMPTZ '2026-03-09 09:55:00+02'),

             -- Written text only: no numeric rating.
             ('emma@reverie.com', 'tt5052448', NULL,
              'I do not think a number would say anything useful about this one. Worth going in unprepared.',
              TIMESTAMPTZ '2026-04-12 20:30:00+03'),

             ('emma@reverie.com', 'tt0364569', 8,
              'Brutal and precise. Not a film I will rush to see again, but an impressive one.',
              TIMESTAMPTZ '2026-06-22 10:15:00+03'),

             -- Daniel
             ('daniel@reverie.com', 'tt0110912', 9,
              'The structure is the point. Rearranged chronologically it would lose most of its charm.',
              TIMESTAMPTZ '2026-02-09 18:25:00+02'),

             ('daniel@reverie.com', 'tt0468569', 9,
              'Holds together far better than most films built around a single performance.',
              TIMESTAMPTZ '2026-03-27 21:00:00+02'),

             ('daniel@reverie.com', 'tt1392190', 8,
              'Almost no dialogue and somehow never confusing. Remarkable editing.',
              TIMESTAMPTZ '2026-05-25 12:45:00+03'),

             ('daniel@reverie.com', 'tt0816692', 7,
              'Ambitious, occasionally overwrought, still worth the running time.',
              TIMESTAMPTZ '2026-06-16 08:30:00+03')
     ) AS entry (email, imdb_id, rating, review_text, written_at)
         JOIN users u ON u.email = entry.email
         JOIN movies m ON m.imdb_id = entry.imdb_id;
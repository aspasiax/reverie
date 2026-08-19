-- =========================================================
-- V907: Seed more demo readers
-- =========================================================
-- Adds six further demonstration accounts, together with the
-- films they watched and what they thought of them.
--
-- Three readers were enough to show that the application works.
-- They were not enough to show what it is for: their tastes
-- barely overlapped, so almost every film carried a single
-- rating, and a single rating has no distribution, no
-- disagreement and nothing to read. These six deliberately
-- watch the same films as each other and as the original
-- three, which is what gives a film an average worth showing.
--
-- The same rules V904 respects are respected here:
--
--   * every review has a matching watch log for the same
--     user and film
--   * no user reviews the same film twice
--
-- Some readers also log films they never reviewed, so a watch
-- history does not read as a list of reviews with the text
-- removed.
--
-- Every account carries the same BCrypt hash as alex, and so
-- the same demo password. Six different hashes of one password
-- would make the file longer without making it truer.
--
-- Demo credentials: <username>@reverie.com / User123!
--   nora, theo, iris, milo, ruth, sam
--
-- Rows are resolved through natural keys so the file never
-- depends on generated identifiers.
-- =========================================================


-- =========================================================
-- Readers
-- =========================================================

INSERT INTO users (
    uuid,
    username,
    email,
    password,
    display_name,
    enabled,
    role_id,
    created_at,
    updated_at,
    deleted,
    deleted_at
)
VALUES
    (
        gen_random_uuid(),
        'nora',
        'nora@reverie.com',
        '$2a$12$.dzDDVAg2ctgu.hRUfRs.OGpcK66lgZM3sqEX287bXkAchWEUWQFO',
        'Nora',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    ),
    (
        gen_random_uuid(),
        'theo',
        'theo@reverie.com',
        '$2a$12$.dzDDVAg2ctgu.hRUfRs.OGpcK66lgZM3sqEX287bXkAchWEUWQFO',
        'Theo',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    ),
    (
        gen_random_uuid(),
        'iris',
        'iris@reverie.com',
        '$2a$12$.dzDDVAg2ctgu.hRUfRs.OGpcK66lgZM3sqEX287bXkAchWEUWQFO',
        'Iris',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    ),
    (
        gen_random_uuid(),
        'milo',
        'milo@reverie.com',
        '$2a$12$.dzDDVAg2ctgu.hRUfRs.OGpcK66lgZM3sqEX287bXkAchWEUWQFO',
        'Milo',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    ),
    (
        gen_random_uuid(),
        'ruth',
        'ruth@reverie.com',
        '$2a$12$.dzDDVAg2ctgu.hRUfRs.OGpcK66lgZM3sqEX287bXkAchWEUWQFO',
        'Ruth',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    ),
    (
        gen_random_uuid(),
        'sam',
        'sam@reverie.com',
        '$2a$12$.dzDDVAg2ctgu.hRUfRs.OGpcK66lgZM3sqEX287bXkAchWEUWQFO',
        'Sam',
        TRUE,
        (SELECT id FROM roles WHERE name = 'USER'),
        NOW(),
        NOW(),
        FALSE,
        NULL
    );


-- =========================================================
-- Watch logs  (91 viewings)
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
             -- Nora
             ('nora@reverie.com', 'tt0816692', DATE '2026-01-06', TIMESTAMPTZ '2026-01-06 21:00:00+02'),
             ('nora@reverie.com', 'tt0078748', DATE '2026-01-24', TIMESTAMPTZ '2026-01-24 21:00:00+02'),
             ('nora@reverie.com', 'tt5052448', DATE '2026-02-11', TIMESTAMPTZ '2026-02-11 21:00:00+02'),
             ('nora@reverie.com', 'tt6751668', DATE '2026-03-02', TIMESTAMPTZ '2026-03-02 21:00:00+02'),
             ('nora@reverie.com', 'tt0468569', DATE '2026-03-20', TIMESTAMPTZ '2026-03-20 21:00:00+02'),
             ('nora@reverie.com', 'tt0133093', DATE '2026-04-07', TIMESTAMPTZ '2026-04-07 21:00:00+03'),
             ('nora@reverie.com', 'tt0137523', DATE '2026-04-26', TIMESTAMPTZ '2026-04-26 21:00:00+03'),
             ('nora@reverie.com', 'tt0364569', DATE '2026-05-14', TIMESTAMPTZ '2026-05-14 21:00:00+03'),
             ('nora@reverie.com', 'tt1392190', DATE '2026-06-01', TIMESTAMPTZ '2026-06-01 21:00:00+03'),
             ('nora@reverie.com', 'tt1375666', DATE '2026-06-20', TIMESTAMPTZ '2026-06-20 21:00:00+03'),
             ('nora@reverie.com', 'tt0111161', DATE '2026-07-08', TIMESTAMPTZ '2026-07-08 21:00:00+03'),
             ('nora@reverie.com', 'tt2543164', DATE '2026-07-26', TIMESTAMPTZ '2026-07-26 21:00:00+03'),

             -- Theo
             ('theo@reverie.com', 'tt0816692', DATE '2026-01-09', TIMESTAMPTZ '2026-01-09 21:00:00+02'),
             ('theo@reverie.com', 'tt0245429', DATE '2026-01-22', TIMESTAMPTZ '2026-01-22 21:00:00+02'),
             ('theo@reverie.com', 'tt5311514', DATE '2026-02-05', TIMESTAMPTZ '2026-02-05 21:00:00+02'),
             ('theo@reverie.com', 'tt3783958', DATE '2026-02-19', TIMESTAMPTZ '2026-02-19 21:00:00+02'),
             ('theo@reverie.com', 'tt6751668', DATE '2026-03-05', TIMESTAMPTZ '2026-03-05 21:00:00+02'),
             ('theo@reverie.com', 'tt0068646', DATE '2026-03-18', TIMESTAMPTZ '2026-03-18 21:00:00+02'),
             ('theo@reverie.com', 'tt0133093', DATE '2026-04-01', TIMESTAMPTZ '2026-04-01 21:00:00+03'),
             ('theo@reverie.com', 'tt0078748', DATE '2026-04-15', TIMESTAMPTZ '2026-04-15 21:00:00+03'),
             ('theo@reverie.com', 'tt2543164', DATE '2026-04-29', TIMESTAMPTZ '2026-04-29 21:00:00+03'),
             ('theo@reverie.com', 'tt6710474', DATE '2026-05-12', TIMESTAMPTZ '2026-05-12 21:00:00+03'),
             ('theo@reverie.com', 'tt2278388', DATE '2026-05-26', TIMESTAMPTZ '2026-05-26 21:00:00+03'),
             ('theo@reverie.com', 'tt0120737', DATE '2026-06-09', TIMESTAMPTZ '2026-06-09 21:00:00+03'),
             ('theo@reverie.com', 'tt1392190', DATE '2026-06-23', TIMESTAMPTZ '2026-06-23 21:00:00+03'),
             ('theo@reverie.com', 'tt1856101', DATE '2026-07-06', TIMESTAMPTZ '2026-07-06 21:00:00+03'),
             ('theo@reverie.com', 'tt1375666', DATE '2026-07-20', TIMESTAMPTZ '2026-07-20 21:00:00+03'),
             ('theo@reverie.com', 'tt0109830', DATE '2026-08-03', TIMESTAMPTZ '2026-08-03 21:00:00+03'),

             -- Iris
             ('iris@reverie.com', 'tt0816692', DATE '2026-01-12', TIMESTAMPTZ '2026-01-12 21:00:00+02'),
             ('iris@reverie.com', 'tt0068646', DATE '2026-01-24', TIMESTAMPTZ '2026-01-24 21:00:00+02'),
             ('iris@reverie.com', 'tt2582802', DATE '2026-02-06', TIMESTAMPTZ '2026-02-06 21:00:00+02'),
             ('iris@reverie.com', 'tt3783958', DATE '2026-02-19', TIMESTAMPTZ '2026-02-19 21:00:00+02'),
             ('iris@reverie.com', 'tt6751668', DATE '2026-03-04', TIMESTAMPTZ '2026-03-04 21:00:00+02'),
             ('iris@reverie.com', 'tt0245429', DATE '2026-03-17', TIMESTAMPTZ '2026-03-17 21:00:00+02'),
             ('iris@reverie.com', 'tt0110912', DATE '2026-03-30', TIMESTAMPTZ '2026-03-30 21:00:00+02'),
             ('iris@reverie.com', 'tt0137523', DATE '2026-04-12', TIMESTAMPTZ '2026-04-12 21:00:00+03'),
             ('iris@reverie.com', 'tt2543164', DATE '2026-04-25', TIMESTAMPTZ '2026-04-25 21:00:00+03'),
             ('iris@reverie.com', 'tt5311514', DATE '2026-05-08', TIMESTAMPTZ '2026-05-08 21:00:00+03'),
             ('iris@reverie.com', 'tt2278388', DATE '2026-05-21', TIMESTAMPTZ '2026-05-21 21:00:00+03'),
             ('iris@reverie.com', 'tt0099685', DATE '2026-06-03', TIMESTAMPTZ '2026-06-03 21:00:00+03'),
             ('iris@reverie.com', 'tt0109830', DATE '2026-06-16', TIMESTAMPTZ '2026-06-16 21:00:00+03'),
             ('iris@reverie.com', 'tt0111161', DATE '2026-06-29', TIMESTAMPTZ '2026-06-29 21:00:00+03'),
             ('iris@reverie.com', 'tt1856101', DATE '2026-07-12', TIMESTAMPTZ '2026-07-12 21:00:00+03'),
             ('iris@reverie.com', 'tt0468569', DATE '2026-07-25', TIMESTAMPTZ '2026-07-25 21:00:00+03'),
             ('iris@reverie.com', 'tt0364569', DATE '2026-08-07', TIMESTAMPTZ '2026-08-07 21:00:00+03'),

             -- Milo
             ('milo@reverie.com', 'tt0816692', DATE '2026-01-15', TIMESTAMPTZ '2026-01-15 21:00:00+02'),
             ('milo@reverie.com', 'tt0137523', DATE '2026-01-29', TIMESTAMPTZ '2026-01-29 21:00:00+02'),
             ('milo@reverie.com', 'tt0068646', DATE '2026-02-13', TIMESTAMPTZ '2026-02-13 21:00:00+02'),
             ('milo@reverie.com', 'tt0468569', DATE '2026-02-28', TIMESTAMPTZ '2026-02-28 21:00:00+02'),
             ('milo@reverie.com', 'tt0110912', DATE '2026-03-14', TIMESTAMPTZ '2026-03-14 21:00:00+02'),
             ('milo@reverie.com', 'tt0133093', DATE '2026-03-29', TIMESTAMPTZ '2026-03-29 21:00:00+02'),
             ('milo@reverie.com', 'tt5052448', DATE '2026-04-13', TIMESTAMPTZ '2026-04-13 21:00:00+03'),
             ('milo@reverie.com', 'tt0078748', DATE '2026-04-27', TIMESTAMPTZ '2026-04-27 21:00:00+03'),
             ('milo@reverie.com', 'tt0120737', DATE '2026-05-12', TIMESTAMPTZ '2026-05-12 21:00:00+03'),
             ('milo@reverie.com', 'tt0099685', DATE '2026-05-27', TIMESTAMPTZ '2026-05-27 21:00:00+03'),
             ('milo@reverie.com', 'tt0111161', DATE '2026-06-10', TIMESTAMPTZ '2026-06-10 21:00:00+03'),
             ('milo@reverie.com', 'tt1392190', DATE '2026-06-25', TIMESTAMPTZ '2026-06-25 21:00:00+03'),
             ('milo@reverie.com', 'tt1856101', DATE '2026-07-10', TIMESTAMPTZ '2026-07-10 21:00:00+03'),
             ('milo@reverie.com', 'tt1375666', DATE '2026-07-24', TIMESTAMPTZ '2026-07-24 21:00:00+03'),
             ('milo@reverie.com', 'tt6751668', DATE '2026-08-08', TIMESTAMPTZ '2026-08-08 21:00:00+03'),

             -- Ruth
             ('ruth@reverie.com', 'tt0816692', DATE '2026-01-07', TIMESTAMPTZ '2026-01-07 21:00:00+02'),
             ('ruth@reverie.com', 'tt3783958', DATE '2026-01-17', TIMESTAMPTZ '2026-01-17 21:00:00+02'),
             ('ruth@reverie.com', 'tt0137523', DATE '2026-01-27', TIMESTAMPTZ '2026-01-27 21:00:00+02'),
             ('ruth@reverie.com', 'tt6710474', DATE '2026-02-07', TIMESTAMPTZ '2026-02-07 21:00:00+02'),
             ('ruth@reverie.com', 'tt0111161', DATE '2026-02-17', TIMESTAMPTZ '2026-02-17 21:00:00+02'),
             ('ruth@reverie.com', 'tt6751668', DATE '2026-02-28', TIMESTAMPTZ '2026-02-28 21:00:00+02'),
             ('ruth@reverie.com', 'tt0068646', DATE '2026-03-10', TIMESTAMPTZ '2026-03-10 21:00:00+02'),
             ('ruth@reverie.com', 'tt0468569', DATE '2026-03-21', TIMESTAMPTZ '2026-03-21 21:00:00+02'),
             ('ruth@reverie.com', 'tt0110912', DATE '2026-03-31', TIMESTAMPTZ '2026-03-31 21:00:00+02'),
             ('ruth@reverie.com', 'tt5052448', DATE '2026-04-11', TIMESTAMPTZ '2026-04-11 21:00:00+03'),
             ('ruth@reverie.com', 'tt0364569', DATE '2026-04-21', TIMESTAMPTZ '2026-04-21 21:00:00+03'),
             ('ruth@reverie.com', 'tt2582802', DATE '2026-05-02', TIMESTAMPTZ '2026-05-02 21:00:00+03'),
             ('ruth@reverie.com', 'tt0078748', DATE '2026-05-12', TIMESTAMPTZ '2026-05-12 21:00:00+03'),
             ('ruth@reverie.com', 'tt2543164', DATE '2026-05-23', TIMESTAMPTZ '2026-05-23 21:00:00+03'),
             ('ruth@reverie.com', 'tt0120737', DATE '2026-06-02', TIMESTAMPTZ '2026-06-02 21:00:00+03'),
             ('ruth@reverie.com', 'tt0099685', DATE '2026-06-13', TIMESTAMPTZ '2026-06-13 21:00:00+03'),
             ('ruth@reverie.com', 'tt0109830', DATE '2026-06-23', TIMESTAMPTZ '2026-06-23 21:00:00+03'),
             ('ruth@reverie.com', 'tt1375666', DATE '2026-07-04', TIMESTAMPTZ '2026-07-04 21:00:00+03'),
             ('ruth@reverie.com', 'tt0133093', DATE '2026-07-14', TIMESTAMPTZ '2026-07-14 21:00:00+03'),
             ('ruth@reverie.com', 'tt5311514', DATE '2026-07-25', TIMESTAMPTZ '2026-07-25 21:00:00+03'),
             ('ruth@reverie.com', 'tt2278388', DATE '2026-08-04', TIMESTAMPTZ '2026-08-04 21:00:00+03'),

             -- Sam
             ('sam@reverie.com', 'tt0816692', DATE '2026-01-10', TIMESTAMPTZ '2026-01-10 21:00:00+02'),
             ('sam@reverie.com', 'tt3783958', DATE '2026-02-01', TIMESTAMPTZ '2026-02-01 21:00:00+02'),
             ('sam@reverie.com', 'tt2278388', DATE '2026-02-23', TIMESTAMPTZ '2026-02-23 21:00:00+02'),
             ('sam@reverie.com', 'tt0245429', DATE '2026-03-17', TIMESTAMPTZ '2026-03-17 21:00:00+02'),
             ('sam@reverie.com', 'tt2582802', DATE '2026-04-08', TIMESTAMPTZ '2026-04-08 21:00:00+03'),
             ('sam@reverie.com', 'tt6710474', DATE '2026-04-30', TIMESTAMPTZ '2026-04-30 21:00:00+03'),
             ('sam@reverie.com', 'tt5311514', DATE '2026-05-22', TIMESTAMPTZ '2026-05-22 21:00:00+03'),
             ('sam@reverie.com', 'tt0109830', DATE '2026-06-13', TIMESTAMPTZ '2026-06-13 21:00:00+03'),
             ('sam@reverie.com', 'tt2543164', DATE '2026-07-05', TIMESTAMPTZ '2026-07-05 21:00:00+03'),
             ('sam@reverie.com', 'tt0068646', DATE '2026-07-27', TIMESTAMPTZ '2026-07-27 21:00:00+03')
     ) AS entry (email, imdb_id, watched_at, logged_at)
         JOIN users u ON u.email = entry.email
         JOIN movies m ON m.imdb_id = entry.imdb_id;


-- =========================================================
-- Reviews  (80 reviews)
-- =========================================================
-- Every pair below also appears in the watch logs above, which
-- is the precondition the review service enforces.
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
             -- Nora
             ('nora@reverie.com', 'tt0816692', 9,
              'The silence in the vacuum shots does more work than most horror soundtracks manage in a whole film.',
              TIMESTAMPTZ '2026-01-07 10:30:00+02'),
             ('nora@reverie.com', 'tt0078748', 10,
              'Still the best haunted house ever built, and it happens to be in orbit. Nothing since has bettered the pacing.',
              TIMESTAMPTZ '2026-01-25 10:30:00+02'),
             ('nora@reverie.com', 'tt5052448', 9,
              'Funny until it is not, and the turn is so well hidden that you feel complicit in missing it.',
              TIMESTAMPTZ '2026-02-12 10:30:00+02'),
             ('nora@reverie.com', 'tt6751668', 10,
              NULL,
              TIMESTAMPTZ '2026-03-03 10:30:00+02'),
             ('nora@reverie.com', 'tt0468569', 8,
              NULL,
              TIMESTAMPTZ '2026-03-21 10:30:00+02'),
             ('nora@reverie.com', 'tt0133093', 7,
              NULL,
              TIMESTAMPTZ '2026-04-08 10:30:00+03'),
             ('nora@reverie.com', 'tt0137523', 8,
              NULL,
              TIMESTAMPTZ '2026-04-27 10:30:00+03'),
             ('nora@reverie.com', 'tt0364569', 9,
              NULL,
              TIMESTAMPTZ '2026-05-15 10:30:00+03'),
             ('nora@reverie.com', 'tt1392190', 8,
              NULL,
              TIMESTAMPTZ '2026-06-02 10:30:00+03'),
             ('nora@reverie.com', 'tt1375666', 8,
              NULL,
              TIMESTAMPTZ '2026-06-21 10:30:00+03'),

             -- Theo
             ('theo@reverie.com', 'tt0816692', 8,
              'Enormous ideas, occasionally clumsy dialogue. The images carry it further than the script does.',
              TIMESTAMPTZ '2026-01-10 10:30:00+02'),
             ('theo@reverie.com', 'tt0245429', 10,
              'Every background is a place someone could live in. It never once explains its own rules and never needs to.',
              TIMESTAMPTZ '2026-01-23 10:30:00+02'),
             ('theo@reverie.com', 'tt5311514', 10,
              'Two films politely swapping places halfway through, and both of them are good.',
              TIMESTAMPTZ '2026-02-06 10:30:00+02'),
             ('theo@reverie.com', 'tt3783958', 8,
              'The opening is a promise the rest of the film keeps in a quieter register than expected.',
              TIMESTAMPTZ '2026-02-20 10:30:00+02'),
             ('theo@reverie.com', 'tt6751668', 9,
              NULL,
              TIMESTAMPTZ '2026-03-06 10:30:00+02'),
             ('theo@reverie.com', 'tt0068646', 7,
              NULL,
              TIMESTAMPTZ '2026-03-19 10:30:00+02'),
             ('theo@reverie.com', 'tt0133093', 8,
              NULL,
              TIMESTAMPTZ '2026-04-02 10:30:00+03'),
             ('theo@reverie.com', 'tt0078748', 7,
              NULL,
              TIMESTAMPTZ '2026-04-16 10:30:00+03'),
             ('theo@reverie.com', 'tt2543164', 8,
              NULL,
              TIMESTAMPTZ '2026-04-30 10:30:00+03'),
             ('theo@reverie.com', 'tt6710474', 10,
              NULL,
              TIMESTAMPTZ '2026-05-13 10:30:00+03'),
             ('theo@reverie.com', 'tt2278388', 9,
              NULL,
              TIMESTAMPTZ '2026-05-27 10:30:00+03'),
             ('theo@reverie.com', 'tt0120737', 9,
              NULL,
              TIMESTAMPTZ '2026-06-10 10:30:00+03'),
             ('theo@reverie.com', 'tt1392190', 7,
              NULL,
              TIMESTAMPTZ '2026-06-24 10:30:00+03'),
             ('theo@reverie.com', 'tt1856101', 8,
              NULL,
              TIMESTAMPTZ '2026-07-07 10:30:00+03'),
             ('theo@reverie.com', 'tt1375666', 8,
              NULL,
              TIMESTAMPTZ '2026-07-21 10:30:00+03'),

             -- Iris
             ('iris@reverie.com', 'tt0816692', 6,
              'Admirable reach, and I understand why people love it, but the emotional beats are underlined twice each.',
              TIMESTAMPTZ '2026-01-13 10:30:00+02'),
             ('iris@reverie.com', 'tt0068646', 10,
              'Nothing is rushed and nothing is wasted. Every scene is doing at least two things at once.',
              TIMESTAMPTZ '2026-01-25 10:30:00+02'),
             ('iris@reverie.com', 'tt2582802', 10,
              'A film about the price of being good at something, and honest enough not to decide whether it was worth it.',
              TIMESTAMPTZ '2026-02-07 10:30:00+02'),
             ('iris@reverie.com', 'tt3783958', 6,
              'Charming for an hour. Then it remembers it wants to be about something and the charm thins out.',
              TIMESTAMPTZ '2026-02-20 10:30:00+02'),
             ('iris@reverie.com', 'tt6751668', 9,
              NULL,
              TIMESTAMPTZ '2026-03-05 10:30:00+02'),
             ('iris@reverie.com', 'tt0245429', 8,
              NULL,
              TIMESTAMPTZ '2026-03-18 10:30:00+02'),
             ('iris@reverie.com', 'tt0110912', 9,
              NULL,
              TIMESTAMPTZ '2026-03-31 10:30:00+02'),
             ('iris@reverie.com', 'tt0137523', 6,
              NULL,
              TIMESTAMPTZ '2026-04-13 10:30:00+03'),
             ('iris@reverie.com', 'tt2543164', 9,
              NULL,
              TIMESTAMPTZ '2026-04-26 10:30:00+03'),
             ('iris@reverie.com', 'tt5311514', 7,
              NULL,
              TIMESTAMPTZ '2026-05-09 10:30:00+03'),
             ('iris@reverie.com', 'tt2278388', 8,
              NULL,
              TIMESTAMPTZ '2026-05-22 10:30:00+03'),
             ('iris@reverie.com', 'tt0099685', 9,
              NULL,
              TIMESTAMPTZ '2026-06-04 10:30:00+03'),
             ('iris@reverie.com', 'tt0109830', 8,
              NULL,
              TIMESTAMPTZ '2026-06-17 10:30:00+03'),
             ('iris@reverie.com', 'tt0111161', 10,
              NULL,
              TIMESTAMPTZ '2026-06-30 10:30:00+03'),
             ('iris@reverie.com', 'tt1856101', 8,
              NULL,
              TIMESTAMPTZ '2026-07-13 10:30:00+03'),

             -- Milo
             ('milo@reverie.com', 'tt0816692', 10,
              'Saw it on the largest screen I could find and would do it again tomorrow.',
              TIMESTAMPTZ '2026-01-16 10:30:00+02'),
             ('milo@reverie.com', 'tt0137523', 9,
              'Everyone quotes the wrong parts. It is much funnier and much sadder than its reputation.',
              TIMESTAMPTZ '2026-01-30 10:30:00+02'),
             ('milo@reverie.com', 'tt0068646', 8,
              NULL,
              TIMESTAMPTZ '2026-02-14 10:30:00+02'),
             ('milo@reverie.com', 'tt0468569', 10,
              NULL,
              TIMESTAMPTZ '2026-03-01 10:30:00+02'),
             ('milo@reverie.com', 'tt0110912', 9,
              NULL,
              TIMESTAMPTZ '2026-03-15 10:30:00+02'),
             ('milo@reverie.com', 'tt0133093', 10,
              NULL,
              TIMESTAMPTZ '2026-03-30 10:30:00+02'),
             ('milo@reverie.com', 'tt5052448', 8,
              NULL,
              TIMESTAMPTZ '2026-04-14 10:30:00+03'),
             ('milo@reverie.com', 'tt0078748', 8,
              NULL,
              TIMESTAMPTZ '2026-04-28 10:30:00+03'),
             ('milo@reverie.com', 'tt0120737', 10,
              NULL,
              TIMESTAMPTZ '2026-05-13 10:30:00+03'),
             ('milo@reverie.com', 'tt0099685', 8,
              NULL,
              TIMESTAMPTZ '2026-05-28 10:30:00+03'),
             ('milo@reverie.com', 'tt0111161', 9,
              NULL,
              TIMESTAMPTZ '2026-06-11 10:30:00+03'),
             ('milo@reverie.com', 'tt1392190', 10,
              NULL,
              TIMESTAMPTZ '2026-06-26 10:30:00+03'),
             ('milo@reverie.com', 'tt1856101', 7,
              NULL,
              TIMESTAMPTZ '2026-07-11 10:30:00+03'),
             ('milo@reverie.com', 'tt1375666', 9,
              NULL,
              TIMESTAMPTZ '2026-07-25 10:30:00+03'),

             -- Ruth
             ('ruth@reverie.com', 'tt0816692', 7,
              'Two thirds of a great film and one third of a lecture. I keep meaning to like it more than I do.',
              TIMESTAMPTZ '2026-01-08 10:30:00+02'),
             ('ruth@reverie.com', 'tt3783958', 4,
              'Handsome and hollow. It wants credit for a sad ending it has not earned.',
              TIMESTAMPTZ '2026-01-18 10:30:00+02'),
             ('ruth@reverie.com', 'tt0137523', 5,
              'A film that thinks it is smarter than its audience, aimed at an audience that agrees.',
              TIMESTAMPTZ '2026-01-28 10:30:00+02'),
             ('ruth@reverie.com', 'tt6710474', 6,
              'Exhausting rather than expansive. The last twenty minutes are genuinely moving and I wish they arrived sooner.',
              TIMESTAMPTZ '2026-02-08 10:30:00+02'),
             ('ruth@reverie.com', 'tt0111161', 9,
              'Sentimental, and I do not care. It knows exactly what it is doing to you and does it well.',
              TIMESTAMPTZ '2026-02-18 10:30:00+02'),
             ('ruth@reverie.com', 'tt6751668', 8,
              NULL,
              TIMESTAMPTZ '2026-03-01 10:30:00+02'),
             ('ruth@reverie.com', 'tt0068646', 9,
              NULL,
              TIMESTAMPTZ '2026-03-11 10:30:00+02'),
             ('ruth@reverie.com', 'tt0468569', 7,
              NULL,
              TIMESTAMPTZ '2026-03-22 10:30:00+02'),
             ('ruth@reverie.com', 'tt0110912', 8,
              NULL,
              TIMESTAMPTZ '2026-04-01 10:30:00+03'),
             ('ruth@reverie.com', 'tt5052448', 7,
              NULL,
              TIMESTAMPTZ '2026-04-12 10:30:00+03'),
             ('ruth@reverie.com', 'tt0364569', 8,
              NULL,
              TIMESTAMPTZ '2026-04-22 10:30:00+03'),
             ('ruth@reverie.com', 'tt2582802', 9,
              NULL,
              TIMESTAMPTZ '2026-05-03 10:30:00+03'),
             ('ruth@reverie.com', 'tt0078748', 8,
              NULL,
              TIMESTAMPTZ '2026-05-13 10:30:00+03'),
             ('ruth@reverie.com', 'tt2543164', 7,
              NULL,
              TIMESTAMPTZ '2026-05-24 10:30:00+03'),
             ('ruth@reverie.com', 'tt0120737', 8,
              NULL,
              TIMESTAMPTZ '2026-06-03 10:30:00+03'),
             ('ruth@reverie.com', 'tt0099685', 8,
              NULL,
              TIMESTAMPTZ '2026-06-14 10:30:00+03'),
             ('ruth@reverie.com', 'tt0109830', 6,
              NULL,
              TIMESTAMPTZ '2026-06-24 10:30:00+03'),
             ('ruth@reverie.com', 'tt1375666', 7,
              NULL,
              TIMESTAMPTZ '2026-07-05 10:30:00+03'),

             -- Sam
             ('sam@reverie.com', 'tt0816692', 9,
              'I did not expect to be undone by a scene about a video message, and yet.',
              TIMESTAMPTZ '2026-01-11 10:30:00+02'),
             ('sam@reverie.com', 'tt3783958', 10,
              'The one I put on when nothing else will do. The ending hurts in a way I look forward to.',
              TIMESTAMPTZ '2026-02-02 10:30:00+02'),
             ('sam@reverie.com', 'tt2278388', 9,
              'Every frame arranged like a pastry box, and underneath it a film about losing a whole world.',
              TIMESTAMPTZ '2026-02-24 10:30:00+02'),
             ('sam@reverie.com', 'tt0245429', 9,
              NULL,
              TIMESTAMPTZ '2026-03-18 10:30:00+02'),
             ('sam@reverie.com', 'tt2582802', 8,
              NULL,
              TIMESTAMPTZ '2026-04-09 10:30:00+03'),
             ('sam@reverie.com', 'tt6710474', 8,
              NULL,
              TIMESTAMPTZ '2026-05-01 10:30:00+03'),
             ('sam@reverie.com', 'tt5311514', 9,
              NULL,
              TIMESTAMPTZ '2026-05-23 10:30:00+03'),
             ('sam@reverie.com', 'tt0109830', 9,
              NULL,
              TIMESTAMPTZ '2026-06-14 10:30:00+03')
     ) AS entry (email, imdb_id, rating, review_text, written_at)
         JOIN users u ON u.email = entry.email
         JOIN movies m ON m.imdb_id = entry.imdb_id;


-- =========================================================
-- Profiles
-- =========================================================
-- Each favourite is a film the account watched above.
-- =========================================================

UPDATE users
SET bio = 'Horror and thrillers, the older the better. Holds that a film which explains its monster has already lost.',
    favourite_movie_id = (SELECT id FROM movies WHERE imdb_id = 'tt0078748'),
    updated_at = NOW()
WHERE username = 'nora';

UPDATE users
SET bio = 'Animation first, fantasy second, everything else when someone insists. Keeps a running argument that drawn films age better than photographed ones.',
    favourite_movie_id = (SELECT id FROM movies WHERE imdb_id = 'tt0245429'),
    updated_at = NOW()
WHERE username = 'theo';

UPDATE users
SET bio = 'The long ones, the slow ones, the ones people mean when they say classic. Rewatches more than she watches.',
    favourite_movie_id = (SELECT id FROM movies WHERE imdb_id = 'tt0068646'),
    updated_at = NOW()
WHERE username = 'iris';

UPDATE users
SET bio = 'Here for the ones that move. Will forgive almost any plot for a chase that was actually filmed.',
    favourite_movie_id = (SELECT id FROM movies WHERE imdb_id = 'tt0468569'),
    updated_at = NOW()
WHERE username = 'milo';

UPDATE users
SET bio = 'Watches everything and is generous with her time, less so with her numbers. A seven from her means she finished it and thought about it after.',
    favourite_movie_id = (SELECT id FROM movies WHERE imdb_id = 'tt2582802'),
    updated_at = NOW()
WHERE username = 'ruth';

UPDATE users
SET bio = 'Musicals, romances, and anything willing to be sincere in public. Cries at the same three films every year and is not embarrassed about it.',
    favourite_movie_id = (SELECT id FROM movies WHERE imdb_id = 'tt3783958'),
    updated_at = NOW()
WHERE username = 'sam';
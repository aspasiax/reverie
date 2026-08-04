-- =========================================================
-- V903: Seed demo movies
-- =========================================================
-- Inserts the demonstration film catalogue and connects each
-- film to its genres.
--
-- The films are treated as manually created entries rather than
-- TMDB imports, so tmdb_id is left NULL. This mirrors the case
-- described in the Movie entity, where external identifiers are
-- optional, and it also demonstrates that the partial unique
-- index tolerates many NULL identifiers at once.
--
-- Poster and backdrop paths are intentionally left NULL. They
-- hold TMDB specific relative paths, and inventing values would
-- only produce broken images in the frontend.
--
-- Genre links are resolved through subqueries so that this file
-- never depends on generated primary key values.
-- =========================================================

INSERT INTO movies (
    uuid,
    title,
    original_title,
    overview,
    release_date,
    runtime,
    original_language,
    imdb_id,
    published,
    created_at,
    updated_at,
    deleted
)
VALUES
    (gen_random_uuid(), 'The Shawshank Redemption', 'The Shawshank Redemption',
     'A banker sentenced to life in prison forms an unlikely friendship and quietly holds on to hope.',
     DATE '1994-09-23', 142, 'en', 'tt0111161', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'The Godfather', 'The Godfather',
     'The ageing head of a crime family gradually hands control to his reluctant youngest son.',
     DATE '1972-03-24', 175, 'en', 'tt0068646', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Pulp Fiction', 'Pulp Fiction',
     'Several loosely connected stories from the fringes of Los Angeles unfold out of order.',
     DATE '1994-10-14', 154, 'en', 'tt0110912', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Interstellar', 'Interstellar',
     'A team of explorers travels through a wormhole in search of a new home for humanity.',
     DATE '2014-11-07', 169, 'en', 'tt0816692', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Parasite', 'Gisaengchung',
     'A struggling family gradually inserts itself into the household of a wealthy one.',
     DATE '2019-05-30', 132, 'ko', 'tt6751668', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'The Dark Knight', 'The Dark Knight',
     'A masked vigilante faces an anarchic criminal who wants to prove that anyone can be broken.',
     DATE '2008-07-18', 152, 'en', 'tt0468569', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Inception', 'Inception',
     'A thief who steals secrets from dreams is asked to plant an idea instead of taking one.',
     DATE '2010-07-16', 148, 'en', 'tt1375666', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Fight Club', 'Fight Club',
     'An insomniac office worker and a soap salesman start an underground club with escalating rules.',
     DATE '1999-10-15', 139, 'en', 'tt0137523', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Forrest Gump', 'Forrest Gump',
     'A gentle man from Alabama finds himself present at decades of historical turning points.',
     DATE '1994-07-06', 142, 'en', 'tt0109830', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'The Matrix', 'The Matrix',
     'A programmer discovers that the world he lives in is an elaborate simulation.',
     DATE '1999-03-31', 136, 'en', 'tt0133093', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Goodfellas', 'Goodfellas',
     'A young man rises through an organised crime family and watches it slowly consume him.',
     DATE '1990-09-21', 146, 'en', 'tt0099685', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Spirited Away', 'Sen to Chihiro no kamikakushi',
     'A girl wanders into a world of spirits and must work to free her parents.',
     DATE '2001-07-20', 125, 'ja', 'tt0245429', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Arrival', 'Arrival',
     'A linguist is recruited to communicate with visitors whose language reshapes how she perceives time.',
     DATE '2016-11-11', 116, 'en', 'tt2543164', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Whiplash', 'Whiplash',
     'A young drummer is pushed to his limits by an instructor who believes cruelty produces greatness.',
     DATE '2014-10-10', 106, 'en', 'tt2582802', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'La La Land', 'La La Land',
     'A musician and an aspiring actress fall in love while chasing incompatible ambitions.',
     DATE '2016-12-09', 128, 'en', 'tt3783958', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Mad Max: Fury Road', 'Mad Max: Fury Road',
     'A drifter and a rebel commander flee across a desert wasteland in a single prolonged chase.',
     DATE '2015-05-15', 120, 'en', 'tt1392190', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Blade Runner 2049', 'Blade Runner 2049',
     'A replicant detective uncovers a secret capable of destabilising what remains of society.',
     DATE '2017-10-06', 164, 'en', 'tt1856101', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Get Out', 'Get Out',
     'A weekend visit to a girlfriend''s family turns increasingly and inexplicably wrong.',
     DATE '2017-02-24', 104, 'en', 'tt5052448', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Everything Everywhere All at Once', 'Everything Everywhere All at Once',
     'A laundromat owner discovers she must connect with parallel versions of herself to save reality.',
     DATE '2022-03-25', 139, 'en', 'tt6710474', TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Oldboy', 'Oldeuboi',
     'A man imprisoned for fifteen years without explanation is released and given days to find out why.',
     DATE '2003-11-21', 120, 'ko', 'tt0364569', TRUE, NOW(), NOW(), FALSE);

-- =========================================================
-- Genre assignments
-- =========================================================
-- Each row pairs a film with one of its genres. Both sides are
-- resolved by their natural keys so the statement stays valid
-- regardless of the generated identifiers.
-- =========================================================

INSERT INTO movie_genres (movie_id, genre_id)
SELECT m.id, g.id
FROM (
         VALUES
             ('tt0111161', 'Drama'),

             ('tt0068646', 'Crime'),
             ('tt0068646', 'Drama'),

             ('tt0110912', 'Crime'),
             ('tt0110912', 'Thriller'),

             ('tt0816692', 'Science Fiction'),
             ('tt0816692', 'Adventure'),
             ('tt0816692', 'Drama'),

             ('tt6751668', 'Thriller'),
             ('tt6751668', 'Drama'),

             ('tt0468569', 'Action'),
             ('tt0468569', 'Crime'),
             ('tt0468569', 'Drama'),

             ('tt1375666', 'Science Fiction'),
             ('tt1375666', 'Action'),
             ('tt1375666', 'Thriller'),

             ('tt0137523', 'Drama'),
             ('tt0137523', 'Thriller'),

             ('tt0109830', 'Drama'),
             ('tt0109830', 'Romance'),

             ('tt0133093', 'Science Fiction'),
             ('tt0133093', 'Action'),

             ('tt0099685', 'Crime'),
             ('tt0099685', 'Drama'),

             ('tt0245429', 'Animation'),
             ('tt0245429', 'Fantasy'),
             ('tt0245429', 'Adventure'),

             ('tt2543164', 'Science Fiction'),
             ('tt2543164', 'Drama'),
             ('tt2543164', 'Mystery'),

             ('tt2582802', 'Drama'),

             ('tt3783958', 'Romance'),
             ('tt3783958', 'Drama'),

             ('tt1392190', 'Action'),
             ('tt1392190', 'Adventure'),
             ('tt1392190', 'Science Fiction'),

             ('tt1856101', 'Science Fiction'),
             ('tt1856101', 'Mystery'),
             ('tt1856101', 'Drama'),

             ('tt5052448', 'Horror'),
             ('tt5052448', 'Mystery'),
             ('tt5052448', 'Thriller'),

             ('tt6710474', 'Science Fiction'),
             ('tt6710474', 'Comedy'),
             ('tt6710474', 'Adventure'),

             ('tt0364569', 'Thriller'),
             ('tt0364569', 'Mystery'),
             ('tt0364569', 'Drama')
     ) AS link (imdb_id, genre_name)
         JOIN movies m ON m.imdb_id = link.imdb_id
         JOIN genres g ON g.name = link.genre_name;
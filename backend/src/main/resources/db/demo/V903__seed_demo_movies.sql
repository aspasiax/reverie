-- =========================================================
-- V903: Seed demo movies
-- =========================================================
-- Inserts the demonstration film catalogue and connects each
-- film to its genres.
--
-- The external identifiers and the image paths were retrieved
-- from TMDB once, using the IMDb identifier of each film, and
-- are stored here so that the application never depends on that
-- service at runtime. No API key is needed to run the project.
--
-- Poster and backdrop values are relative TMDB paths. The client
-- builds the full address by prefixing them with the TMDB image
-- base URL and the desired width.
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
    tmdb_id,
    imdb_id,
    poster_path,
    backdrop_path,
    published,
    created_at,
    updated_at,
    deleted
)
VALUES
    (gen_random_uuid(), 'The Shawshank Redemption', 'The Shawshank Redemption',
     'A banker sentenced to life in prison forms an unlikely friendship and quietly holds on to hope.',
     DATE '1994-09-23', 142, 'en', 278, 'tt0111161',
     '/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg', '/zfbjgQE1uSd9wiPTX4VzsLi0rGG.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'The Godfather', 'The Godfather',
     'The ageing head of a crime family gradually hands control to his reluctant youngest son.',
     DATE '1972-03-24', 175, 'en', 238, 'tt0068646',
     '/3bhkrj58Vtu7enYsRolD1fZdja1.jpg', '/tSPT36ZKlP2WVHJLM4cQPLSzv3b.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Pulp Fiction', 'Pulp Fiction',
     'Several loosely connected stories from the fringes of Los Angeles unfold out of order.',
     DATE '1994-10-14', 154, 'en', 680, 'tt0110912',
     '/vQWk5YBFWF4bZaofAbv0tShwBvQ.jpg', '/suaEOtk1N1sgg2MTM7oZd2cfVp3.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Interstellar', 'Interstellar',
     'A team of explorers travels through a wormhole in search of a new home for humanity.',
     DATE '2014-11-07', 169, 'en', 157336, 'tt0816692',
     '/yQvGrMoipbRoddT0ZR8tPoR7NfX.jpg', '/5XNQBqnBwPA9yT0jZ0p3s8bbLh0.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Parasite', 'Gisaengchung',
     'A struggling family gradually inserts itself into the household of a wealthy one.',
     DATE '2019-05-30', 132, 'ko', 496243, 'tt6751668',
     '/7IiTTgloJzvGI1TAYymCfbfl3vT.jpg', '/vbC0rzdrb7Ohc2TkbEbxtOABECe.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'The Dark Knight', 'The Dark Knight',
     'A masked vigilante faces an anarchic criminal who wants to prove that anyone can be broken.',
     DATE '2008-07-18', 152, 'en', 155, 'tt0468569',
     '/qJ2tW6WMUDux911r6m7haRef0WH.jpg', '/dqK9Hag1054tghRQSqLSfrkvQnA.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Inception', 'Inception',
     'A thief who steals secrets from dreams is asked to plant an idea instead of taking one.',
     DATE '2010-07-16', 148, 'en', 27205, 'tt1375666',
     '/xlaY2zyzMfkhk0HSC5VUwzoZPU1.jpg', '/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Fight Club', 'Fight Club',
     'An insomniac office worker and a soap salesman start an underground club with escalating rules.',
     DATE '1999-10-15', 139, 'en', 550, 'tt0137523',
     '/jSziioSwPVrOy9Yow3XhWIBDjq1.jpg', '/c6OLXfKAk5BKeR6broC8pYiCquX.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Forrest Gump', 'Forrest Gump',
     'A gentle man from Alabama finds himself present at decades of historical turning points.',
     DATE '1994-07-06', 142, 'en', 13, 'tt0109830',
     '/Cw4hIUIAmSYfK9QfaUW5igp9La.jpg', '/66Kn4XWhkuPkJxOJyPEx4U2CUfN.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'The Matrix', 'The Matrix',
     'A programmer discovers that the world he lives in is an elaborate simulation.',
     DATE '1999-03-31', 136, 'en', 603, 'tt0133093',
     '/dXNAPwY7VrqMAo51EKhhCJfaGb5.jpg', '/tlm8UkiQsitc8rSuIAscQDCnP8d.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Goodfellas', 'Goodfellas',
     'A young man rises through an organised crime family and watches it slowly consume him.',
     DATE '1990-09-21', 146, 'en', 769, 'tt0099685',
     '/9OkCLM73MIU2CrKZbqiT8Ln1wY2.jpg', '/gILte6Zd7m1YneIr6MVhh30S9pr.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Spirited Away', 'Sen to Chihiro no kamikakushi',
     'A girl wanders into a world of spirits and must work to free her parents.',
     DATE '2001-07-20', 125, 'ja', 129, 'tt0245429',
     '/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg', '/dyJvKsNs2KP8qQnAXbRwDjblViy.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Arrival', 'Arrival',
     'A linguist is recruited to communicate with visitors whose language reshapes how she perceives time.',
     DATE '2016-11-11', 116, 'en', 329865, 'tt2543164',
     '/x2FJsf1ElAgr63Y3PNPtJrcmpoe.jpg', '/8MUZz7oPXQftFTslZpRP3CVMOoq.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Whiplash', 'Whiplash',
     'A young drummer is pushed to his limits by an instructor who believes cruelty produces greatness.',
     DATE '2014-10-10', 106, 'en', 244786, 'tt2582802',
     '/7fn624j5lj3xTme2SgiLCeuedmO.jpg', '/wbQa0EnWUyRzQ5d1pHLNRlmsCUP.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'La La Land', 'La La Land',
     'A musician and an aspiring actress fall in love while chasing incompatible ambitions.',
     DATE '2016-12-09', 128, 'en', 313369, 'tt3783958',
     '/uDO8zWDhfWwoFdKS4fzkUJt0Rf0.jpg', '/nlPCdZlHtRNcF6C9hzUH4ebmV1w.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Mad Max: Fury Road', 'Mad Max: Fury Road',
     'A drifter and a rebel commander flee across a desert wasteland in a single prolonged chase.',
     DATE '2015-05-15', 120, 'en', 76341, 'tt1392190',
     '/ulcAi4dKpAjHwYGS08vNyx9H6I9.jpg', '/uT895WNwm0aIJRtGizcQhrejWUo.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Blade Runner 2049', 'Blade Runner 2049',
     'A replicant detective uncovers a secret capable of destabilising what remains of society.',
     DATE '2017-10-06', 164, 'en', 335984, 'tt1856101',
     '/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg', '/gNdLJU9TxrpGx4dkZidjys3fyy0.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Get Out', 'Get Out',
     'A weekend visit to a girlfriend''s family turns increasingly and inexplicably wrong.',
     DATE '2017-02-24', 104, 'en', 419430, 'tt5052448',
     '/tFXcEccSQMf3lfhfXKSU9iRBpa3.jpg', '/o8dPH0ZSIyyViP6rjRX1djwCUwI.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Everything Everywhere All at Once', 'Everything Everywhere All at Once',
     'A laundromat owner discovers she must connect with parallel versions of herself to save reality.',
     DATE '2022-03-25', 139, 'en', 545611, 'tt6710474',
     '/u68AjlvlutfEIcpmbYpKcdi09ut.jpg', '/ss0Os3uWJfQAENILHZUdX8Tt1OC.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Oldboy', 'Oldeuboi',
     'A man imprisoned for fifteen years without explanation is released and given days to find out why.',
     DATE '2003-11-21', 120, 'ko', 670, 'tt0364569',
     '/pWDtjs568ZfOTMbURQBYuT4Qxka.jpg', '/sdwjQEM869JFwMytTmvr6ggvaUl.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'The Grand Budapest Hotel', 'The Grand Budapest Hotel',
     'A hotel concierge and his protege are drawn into the theft of a painting and a battle over a fortune.',
     DATE '2014-03-07', 99, 'en', 120467, 'tt2278388',
     '/eWdyYQreja6JGCzqHWXpWHDrrPo.jpg', '/9udCLTxTFl28RxnK8Q05E154ZGa.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'The Lord of the Rings: The Fellowship of the Ring',
     'The Lord of the Rings: The Fellowship of the Ring',
     'A reluctant hobbit sets out to destroy a ring that would let its maker rule everything.',
     DATE '2001-12-19', 178, 'en', 120, 'tt0120737',
     '/6oom5QYQ2yQTMJIbnvbkBL9cHo6.jpg', '/mWDdRXTivGE7aaY2vo1Ie0PfCX5.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Alien', 'Alien',
     'The crew of a commercial towing ship answers a distress call and brings something aboard with them.',
     DATE '1979-05-25', 117, 'en', 348, 'tt0078748',
     '/vfrQk5IPloGg1v9Rzbh2Eg3VGyM.jpg', '/AmR3JG1VQVxU8TfAvljUhfSFUOx.jpg',
     TRUE, NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Your Name.', 'Kimi no na wa.',
     'Two teenagers who have never met discover they are trading places while they sleep.',
     DATE '2016-08-26', 106, 'ja', 372058, 'tt5311514',
     '/q719jXXEzOoYaps6babgKnONONX.jpg', '/8x9iKH8kWA0zdkgNdpAew7OstYe.jpg',
     TRUE, NOW(), NOW(), FALSE);


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
             ('tt0364569', 'Drama'),

             ('tt2278388', 'Comedy'),
             ('tt2278388', 'Adventure'),
             ('tt2278388', 'Drama'),

             ('tt0120737', 'Fantasy'),
             ('tt0120737', 'Adventure'),
             ('tt0120737', 'Action'),

             ('tt0078748', 'Horror'),
             ('tt0078748', 'Science Fiction'),
             ('tt0078748', 'Thriller'),

             ('tt5311514', 'Animation'),
             ('tt5311514', 'Romance'),
             ('tt5311514', 'Fantasy')
     ) AS link (imdb_id, genre_name)
         JOIN movies m ON m.imdb_id = link.imdb_id
         JOIN genres g ON g.name = link.genre_name;
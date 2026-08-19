-- =========================================================
-- V906: Seed demo profiles
-- =========================================================
-- Fills in what the demonstration accounts say about
-- themselves: a short biography and the film each of them
-- names as a favourite.
--
-- Written as an update rather than folded into V901 because
-- that migration has already run everywhere, and an applied
-- migration is never edited.
--
-- Each favourite is a film the account has actually watched,
-- which is the rule the service enforces. Two of them are
-- films the account watched twice: a rewatch is the clearest
-- evidence the demo data offers about what someone loves.
--
-- The profile image is deliberately left empty. The interface
-- falls back to the initial on a colour derived from the
-- handle, and a demo dataset showing that default is more
-- honest than one hotlinking three photographs.
-- =========================================================

UPDATE users
SET bio = 'Science fiction mostly, and anything that treats a long silence as a scene. Keeps a list of films to rewatch, which is not the same list as the watchlist.',
    favourite_movie_id = (SELECT id FROM movies WHERE title = 'Interstellar'),
    updated_at = NOW()
WHERE username = 'alex';

UPDATE users
SET bio = 'Animation, subtitles, and the occasional musical. Believes a film has worked if she can still hear it the next morning.',
    favourite_movie_id = (SELECT id FROM movies WHERE title = 'Spirited Away'),
    updated_at = NOW()
WHERE username = 'emma';

UPDATE users
SET bio = 'Crime, mostly, the kind where everyone talks too much and it turns out to matter. Suspicious of any film under ninety minutes.',
    favourite_movie_id = (SELECT id FROM movies WHERE title = 'Pulp Fiction'),
    updated_at = NOW()
WHERE username = 'daniel';
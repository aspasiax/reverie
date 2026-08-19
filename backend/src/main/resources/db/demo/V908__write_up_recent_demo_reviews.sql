-- =========================================================
-- V908: Write up recent demo reviews
-- =========================================================
-- Gives words to twelve reviews that carried only a number.
--
-- V907 listed each reader's written reviews before their
-- rating-only ones, and then dated them in that order. The
-- effect was invisible until the activity feed was built:
-- the feed shows what happened most recently, and everything
-- recent was a bare score. The one screen meant for reading
-- what people thought had nothing on it to read.
--
-- Dates are left exactly where they are. Moving a review
-- earlier could place it before the viewing it describes,
-- and the demo data is worth more when it could still have
-- been produced through the API.
--
-- Four readers now disagree about Inception and three about
-- Forrest Gump, which is the point: a feed of people who all
-- think the same thing is a list.
--
-- The written text is added as though it had always been
-- there, so updated_at stays equal to created_at rather than
-- marking every one of these as edited.
-- =========================================================

UPDATE reviews r
SET review_text = entry.review_text,
    updated_at = r.created_at
    FROM (
         VALUES
             -- Inception: four readers, four different reservations.
             ('milo@reverie.com', 'tt1375666',
              'The rules are explained twice and I still could not tell you what happens on the third level. It did not matter once. Somebody thought about where the camera goes.'),

             ('theo@reverie.com', 'tt1375666',
              'Astonishing to look at and strangely bloodless underneath. Every character is a function of the plot, which is forgivable when the plot is this good.'),

             ('ruth@reverie.com', 'tt1375666',
              'Immaculate machinery. I admire it more than I enjoy it, and the emotional core it keeps returning to has never once landed for me.'),

             ('nora@reverie.com', 'tt1375666',
              'Closer to a heist film than the poster admits, and better for it. The tension holds even in the stretches where the rules do not.'),

             -- Blade Runner 2049
             ('iris@reverie.com', 'tt1856101',
              'Three hours that never once hurried, which I valued more than the story being told. The images will outlast the plot.'),

             ('theo@reverie.com', 'tt1856101',
              'A film of enormous rooms and very few people. It earns its length in atmosphere rather than in incident.'),

             -- The Shawshank Redemption
             ('iris@reverie.com', 'tt0111161',
              'I have rewatched it more than any film I own and it has never once felt long. It knows exactly when to stop explaining itself.'),

             -- Mad Max: Fury Road
             ('milo@reverie.com', 'tt1392190',
              'Two hours of a single chase and I was not lost for a second. This is what the form is for.'),

             ('theo@reverie.com', 'tt1392190',
              'Astonishing to watch and thin to think about. I keep meaning to rate it higher and then remembering the middle.'),

             -- Forrest Gump: the widest disagreement in the dataset.
             ('ruth@reverie.com', 'tt0109830',
              'Charming and evasive. It walks past every question it raises, and it was forgiven because the walk is lovely.'),

             ('sam@reverie.com', 'tt0109830',
              'I know every objection to it and I do not care. The bench, the letter, the running. It gets me every time.'),

             ('iris@reverie.com', 'tt0109830',
              'Better made than its reputation among the people who dislike it, and less profound than its reputation among the people who love it.')
     ) AS entry (email, imdb_id, review_text)
         JOIN users u ON u.email = entry.email
    JOIN movies m ON m.imdb_id = entry.imdb_id
WHERE r.user_id = u.id
  AND r.movie_id = m.id
  AND r.deleted = FALSE;
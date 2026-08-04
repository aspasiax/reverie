-- =========================================================
-- V902: Seed demo genres
-- =========================================================
-- Inserts the genre catalogue used by the demonstration data.
--
-- Icons hold the name of a frontend icon rather than an image
-- path, and colours are stored as uppercase hexadecimal values
-- so that the frontend can render consistent genre badges.
-- =========================================================

INSERT INTO genres (
    uuid,
    name,
    description,
    icon,
    color,
    created_at,
    updated_at,
    deleted
)
VALUES
    (gen_random_uuid(), 'Action',
     'Fast paced films driven by physical feats, chases and confrontation.',
     'swords', '#E11D48', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Adventure',
     'Journeys into unfamiliar places, often far from home.',
     'compass', '#F97316', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Animation',
     'Stories told through animated images rather than live action.',
     'sparkles', '#8B5CF6', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Comedy',
     'Films built around humour and situations meant to amuse.',
     'laugh', '#FACC15', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Crime',
     'Stories centred on offences, investigations and the people behind them.',
     'fingerprint', '#64748B', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Drama',
     'Character driven stories focused on emotional and personal conflict.',
     'masks', '#0EA5E9', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Fantasy',
     'Worlds shaped by magic and rules that do not apply to our own.',
     'wand', '#A855F7', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Horror',
     'Films designed to unsettle, frighten or disturb the viewer.',
     'ghost', '#1F2937', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Mystery',
     'Puzzles revealed gradually, where the question matters as much as the answer.',
     'search', '#14B8A6', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Romance',
     'Stories in which a relationship sits at the centre of the plot.',
     'heart', '#EC4899', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Science Fiction',
     'Speculative stories grounded in technology, science or possible futures.',
     'rocket', '#3B82F6', NOW(), NOW(), FALSE),

    (gen_random_uuid(), 'Thriller',
     'Tension driven films that rely on suspense and rising stakes.',
     'zap', '#DC2626', NOW(), NOW(), FALSE);
-- =========================================================
-- V23: Correct the crime genre icon
-- =========================================================
-- The demonstration data named this icon "fingerprint", which
-- was its name in the icon library at the time. The library
-- has since renamed it, so the stored value points at nothing
-- and the genre falls back to a plain dot.
--
-- Corrected here rather than by editing V902: an applied
-- migration is never changed, because Flyway records a
-- checksum of each one and every database that has already
-- run it would disagree.
-- =========================================================

UPDATE genres
SET icon       = 'fingerprint-pattern',
    updated_at = NOW()
WHERE icon = 'fingerprint';
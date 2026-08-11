-- =========================================================
-- V18: Rename the user deletion capability
-- =========================================================
-- Accounts are never removed from Reverie. A review belongs to the
-- conversation around a film, and erasing the account that wrote it
-- would quietly rewrite that conversation. What an administrator can
-- do instead is withdraw an account from use: it stops signing in,
-- and everything it contributed stays where it is.
--
-- The capability is therefore renamed to say what it actually permits.
-- Renaming rather than replacing keeps the grants already recorded in
-- role_capabilities, which reference this row by id, in force.
-- =========================================================

UPDATE capabilities
SET name        = 'USER_DISABLE',
    description = 'Allows disabling and re-enabling user accounts.',
    updated_at  = NOW()
WHERE name = 'USER_DELETE';
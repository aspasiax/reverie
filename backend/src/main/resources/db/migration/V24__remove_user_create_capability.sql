-- =========================================================
-- V24: Remove the user creation capability
-- =========================================================
-- USER_CREATE was seeded in V9 and granted to ADMIN in V10,
-- and has never guarded anything.
--
-- Creating an account does happen, of course: it is what
-- registration is. But registration cannot check a
-- capability and never will be able to. Capabilities come
-- from the role attached to a signed in account, and whoever
-- is registering has no account yet, so no role, so no
-- capabilities. A check there would fail for everyone
-- forever. That is why registration lives on the public
-- authentication endpoint rather than under /api/users.
--
-- An administrator could instead be given a screen that
-- creates accounts for other people, which would put this
-- capability to work. It would also mean an administrator
-- typing somebody else's password. The application already
-- reaches the same end by a better road: a person registers
-- themselves, and an administrator changes their role
-- afterwards. Nobody has to know a password that is not
-- theirs.
--
-- So the capability guards an action that exists but cannot
-- be guarded, and an action that could be guarded but should
-- not exist. It is removed rather than renamed: V18 renamed
-- USER_DELETE because that action survived under a new name,
-- while this one was never performed at all.
--
-- The grants go first: role_capabilities references this row.
-- =========================================================

DELETE FROM role_capabilities
WHERE capability_id = (SELECT id FROM capabilities WHERE name = 'USER_CREATE');

DELETE FROM capabilities
WHERE name = 'USER_CREATE';
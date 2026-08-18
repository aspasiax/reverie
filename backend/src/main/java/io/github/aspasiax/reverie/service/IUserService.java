package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.user.*;
import io.github.aspasiax.reverie.exception.InvalidCurrentPasswordException;
import io.github.aspasiax.reverie.exception.SelfDisableException;
import io.github.aspasiax.reverie.exception.UserNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Provides the operations available for user profiles and for the
 * administration of user accounts.
 */
public interface IUserService {

    /**
     * Returns the profile of the authenticated user.
     *
     * @return the authenticated user's own profile
     */
    UserProfileResponse getCurrentUserProfile();

    /**
     * Updates the profile of the authenticated user.
     *
     * @param request the profile update request
     * @return the updated profile
     */
    UserProfileResponse updateCurrentUserProfile(UpdateUserRequest request);

    /**
     * Changes the password of the authenticated user.
     *
     * <p>Tokens already issued remain valid until they expire. Reverie
     * authenticates without server side sessions, so there is no list of
     * live tokens to revoke.</p>
     *
     * @param request the current password and the one to replace it with
     * @throws InvalidCurrentPasswordException if the current password is wrong
     */
    void changePassword(ChangePasswordRequest request);

    /**
     * Returns the publicly visible profile of a user.
     *
     * @param uuid the public user identifier
     * @return the public profile of the user
     */
    UserSummaryResponse findByUuid(UUID uuid);

    /**
     * Retrieves every user account for administration.
     *
     * <p>The returned view carries the email address and the role, which is
     * what makes the accounts manageable, and is therefore restricted to
     * callers holding the user read capability.</p>
     *
     * @return a list containing every account
     */
    List<UserAdminResponse> findAll();

    /**
     * Changes the role assigned to a user.
     *
     * @param uuid    the public identifier of the user
     * @param request the requested role
     * @return the account after the change
     */
    UserAdminResponse updateUserRole(UUID uuid, UpdateUserRoleRequest request);

    /**
     * Allows a previously disabled account to sign in again.
     *
     * @param uuid the public identifier of the user
     * @return the account after the change
     * @throws UserNotFoundException if no user exists
     */
    UserAdminResponse enable(UUID uuid);

    /**
     * Withdraws an account from use without removing anything it created.
     *
     * @param uuid the public identifier of the user
     * @return the account after the change
     * @throws UserNotFoundException  if no user exists
     * @throws SelfDisableException   if the caller is disabling themselves
     */
    UserAdminResponse disable(UUID uuid);
}
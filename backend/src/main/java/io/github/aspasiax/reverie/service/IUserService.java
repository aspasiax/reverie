package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.user.*;

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
}
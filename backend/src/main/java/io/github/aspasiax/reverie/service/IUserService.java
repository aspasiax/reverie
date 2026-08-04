package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.user.UpdateUserRequest;
import io.github.aspasiax.reverie.dto.user.UpdateUserRoleRequest;
import io.github.aspasiax.reverie.dto.user.UserProfileResponse;
import io.github.aspasiax.reverie.dto.user.UserSummaryResponse;

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
     * Returns every active user.
     *
     * @return the active users ordered by username
     */
    List<UserSummaryResponse> findAll();

    /**
     * Assigns a different security role to a user.
     *
     * <p>This is the operation through which additional administrators are
     * created. Administrators may not change their own role, which prevents
     * the last administrator from removing their own privileges.</p>
     *
     * @param uuid    the public identifier of the user to modify
     * @param request the role to assign
     * @return the updated profile of the modified user
     */
    UserProfileResponse updateUserRole(UUID uuid, UpdateUserRoleRequest request);
}
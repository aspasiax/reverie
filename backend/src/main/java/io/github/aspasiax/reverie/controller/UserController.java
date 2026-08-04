package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.user.UpdateUserRequest;
import io.github.aspasiax.reverie.dto.user.UpdateUserRoleRequest;
import io.github.aspasiax.reverie.dto.user.UserProfileResponse;
import io.github.aspasiax.reverie.dto.user.UserSummaryResponse;
import io.github.aspasiax.reverie.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Exposes REST endpoints for user profiles and account administration.
 *
 * <p>
 * The endpoints under {@code /me} act on the authenticated user and
 * therefore require authentication only. Listing all accounts and changing
 * roles are administrative operations guarded by the corresponding user
 * capabilities.
 * </p>
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Users",
        description = "Operations for user profiles and account administration."
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    /**
     * Returns the profile of the authenticated user.
     *
     * @return the authenticated user's own profile
     */
    @Operation(
            summary = "Get my profile",
            description = "Returns the profile of the authenticated user, including private fields such as the email address."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    /**
     * Updates the profile of the authenticated user.
     *
     * @param request the validated profile update request
     * @return the updated profile
     */
    @Operation(
            summary = "Update my profile",
            description = "Updates the display name, biography and profile image of the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid profile data"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(
                userService.updateCurrentUserProfile(request)
        );
    }

    /**
     * Returns the publicly visible profile of a user.
     *
     * @param uuid the user UUID
     * @return the public profile of the user
     */
    @Operation(
            summary = "Get a user profile",
            description = "Returns the publicly visible profile of a user. The email address and the assigned role are not included."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<UserSummaryResponse> findByUuid(
            @PathVariable UUID uuid
    ) {
        return ResponseEntity.ok(userService.findByUuid(uuid));
    }

    /**
     * Returns all active users.
     *
     * @return the active users ordered by username
     */
    @Operation(
            summary = "Get all users",
            description = "Returns every active account. This is an administrative operation."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<List<UserSummaryResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Assigns a different security role to a user.
     *
     * @param uuid    the UUID of the user to modify
     * @param request the validated role assignment request
     * @return the updated profile of the modified user
     */
    @Operation(
            summary = "Change a user's role",
            description = "Assigns a different security role to a user. This is how additional administrators are created."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid role data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User or role not found"),
            @ApiResponse(responseCode = "409", description = "Administrators cannot change their own role")
    })
    @PutMapping("/{uuid}/role")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserProfileResponse> updateUserRole(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        return ResponseEntity.ok(
                userService.updateUserRole(uuid, request)
        );
    }
}
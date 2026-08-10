package io.github.aspasiax.reverie.mapper;

import io.github.aspasiax.reverie.domain.Capability;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.user.UpdateUserRequest;
import io.github.aspasiax.reverie.dto.user.UserProfileResponse;
import io.github.aspasiax.reverie.dto.user.UserSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Maps user entities to API response DTOs and applies profile update
 * requests to existing users.
 *
 * <p>Two response shapes exist on purpose. The profile response is returned
 * only to the account owner and carries private fields, while the summary
 * response is safe to show to any authenticated user.</p>
 */
@Component
public class UserMapper {

    /**
     * Maps a user to the private profile response.
     *
     * @param user the user entity
     * @return the profile of the account owner
     */
    public UserProfileResponse toProfileResponse(User user) {
        return new UserProfileResponse(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getBio(),
                user.getProfileImageUrl(),
                user.getRole().getName(),
                user.getCreatedAt(),
                user.getRole().getCapabilities().stream()
                        .map(Capability::getName)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Maps a user to the publicly visible summary response.
     *
     * @param user the user entity
     * @return the public profile of the user
     */
    public UserSummaryResponse toSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getUuid(),
                user.getUsername(),
                user.getDisplayName(),
                user.getBio(),
                user.getProfileImageUrl(),
                user.getCreatedAt()
        );
    }

    /**
     * Applies the values of an update request to an existing user.
     *
     * <p>The username, email address, password and role are not modified
     * by this method.</p>
     *
     * @param user    the existing user entity
     * @param request the profile update request
     */
    public void updateEntity(
            User user,
            UpdateUserRequest request
    ) {
        user.setDisplayName(request.displayName().trim());
        user.setBio(trimToNull(request.bio()));
        user.setProfileImageUrl(trimToNull(request.profileImageUrl()));
    }

    /**
     * Trims a string and converts blank values to {@code null}.
     *
     * @param value the value to normalize
     * @return the trimmed value or {@code null} when blank
     */
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
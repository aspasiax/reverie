package io.github.aspasiax.reverie.mapper;

import io.github.aspasiax.reverie.domain.Capability;
import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.movie.MovieSummaryResponse;
import io.github.aspasiax.reverie.dto.user.UpdateUserRequest;
import io.github.aspasiax.reverie.dto.user.UserAdminResponse;
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
                toMovieSummary(user.getFavouriteMovie()),
                user.getRole().getName(),
                user.getCreatedAt(),
                user.getRole().getCapabilities().stream()
                        .map(Capability::getName)
                        .collect(Collectors.toSet())
        );
    }

    /**
     * Maps a user to the administrative response.
     *
     * @param user the user entity
     * @return the account as an administrator sees it
     */
    public UserAdminResponse toAdminResponse(User user) {
        return new UserAdminResponse(
                user.getUuid(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole().getName(),
                user.isEnabled(),
                user.getCreatedAt()
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
                toMovieSummary(user.getFavouriteMovie()),
                user.getCreatedAt()
        );
    }

    /**
     * Applies the values of an update request to an existing user.
     *
     * <p>The username, email address, password and role are not modified
     * by this method, and neither is the favourite film: choosing one has
     * to be checked against the watch history of the account, which is a
     * question for the service rather than for a mapper.</p>
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
     * Maps the film a user named as their favourite.
     *
     * <p>A film that has since been removed from the catalogue is reported
     * as no favourite at all. The choice itself is kept, so it returns if
     * the film is restored, but a profile never points at a film that can
     * no longer be opened.</p>
     *
     * @param movie the favourite film, or {@code null} when none was named
     * @return the compact film, or {@code null} when there is none to show
     */
    private MovieSummaryResponse toMovieSummary(Movie movie) {
        if (movie == null || movie.isDeleted()) {
            return null;
        }

        return new MovieSummaryResponse(
                movie.getUuid(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getPosterPath()
        );
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
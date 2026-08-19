package io.github.aspasiax.reverie.dto.user;

import io.github.aspasiax.reverie.dto.movie.MovieSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents the publicly visible profile of a user.
 *
 * <p>The email address and the assigned role are deliberately omitted, so
 * that this response can be shown to any authenticated user without
 * exposing private account information.</p>
 */
@Schema(
        name = "UserSummaryResponse",
        description = "Publicly visible information about a user."
)
public record UserSummaryResponse(

        @Schema(description = "Public UUID of the user.")
        UUID uuid,

        @Schema(description = "Unique public handle of the user.", example = "emma")
        String username,

        @Schema(description = "Name displayed on the profile.", example = "Emma")
        String displayName,

        @Schema(description = "Optional short biography.")
        String bio,

        @Schema(description = "Optional URL of the profile image.")
        String profileImageUrl,

        @Schema(description = "The film the user named as their favourite, if any.")
        MovieSummaryResponse favouriteMovie,

        @Schema(description = "When the account was created.")
        Instant createdAt
) {
}
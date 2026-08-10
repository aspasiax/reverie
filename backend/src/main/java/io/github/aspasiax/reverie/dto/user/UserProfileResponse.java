package io.github.aspasiax.reverie.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the authenticated user's own profile.
 *
 * <p>This response includes the email address, the assigned role and the
 * capabilities it grants, and is therefore only returned to the account
 * owner. The client uses the capabilities to decide which controls to show,
 * mirroring the checks the API performs on every request.</p>
 */
@Schema(
        name = "UserProfileResponse",
        description = "The authenticated user's own profile, including private fields."
)
public record UserProfileResponse(

        @Schema(description = "Public UUID of the user.")
        UUID uuid,

        @Schema(description = "Unique public handle of the user.", example = "emma")
        String username,

        @Schema(description = "Email address used for authentication.", example = "emma@reverie.com")
        String email,

        @Schema(description = "Name displayed on the profile.", example = "Emma")
        String displayName,

        @Schema(description = "Optional short biography.")
        String bio,

        @Schema(description = "Optional URL of the profile image.")
        String profileImageUrl,

        @Schema(description = "Name of the security role assigned to the user.", example = "USER")
        String role,

        @Schema(description = "When the account was created.")
        Instant createdAt,

        @Schema(
                description = "Names of the capabilities granted through the assigned role.",
                example = "[\"MOVIE_READ\", \"REVIEW_CREATE\"]"
        )
        Set<String> capabilities
) {
}
package io.github.aspasiax.reverie.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents the authenticated user's own profile.
 *
 * <p>This response includes the email address and the assigned role, and is
 * therefore only returned to the account owner.</p>
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
        Instant createdAt
) {
}
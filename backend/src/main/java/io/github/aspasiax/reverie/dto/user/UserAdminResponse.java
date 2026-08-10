package io.github.aspasiax.reverie.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a user account as an administrator sees it.
 *
 * <p>This is the third view of a user and sits between the other two. It
 * carries the assigned role, which administration needs, but neither the
 * email address nor the capabilities: changing what an account may do
 * does not require knowing how to reach its owner.</p>
 */
@Schema(
        name = "UserAdminResponse",
        description = "A user account as seen by an administrator."
)
public record UserAdminResponse(

        @Schema(description = "Public UUID of the user.")
        UUID uuid,

        @Schema(description = "Unique public handle of the user.", example = "emma")
        String username,

        @Schema(description = "Name displayed on the profile.", example = "Emma")
        String displayName,

        @Schema(description = "Name of the security role assigned to the user.", example = "USER")
        String role,

        @Schema(description = "Whether the account may sign in.", example = "true")
        boolean enabled,

        @Schema(description = "When the account was created.")
        Instant createdAt
) {
}
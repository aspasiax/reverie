package io.github.aspasiax.reverie.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents a request to assign a different security role to a user.
 *
 * <p>This is how additional administrators are created: an account is
 * registered normally and is then promoted by an existing administrator.</p>
 */
@Schema(
        name = "UpdateUserRoleRequest",
        description = "The security role to assign to a user."
)
public record UpdateUserRoleRequest(

        @Schema(
                description = "Name of the role to assign.",
                example = "ADMIN",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(max = 50)
        String roleName
) {
}
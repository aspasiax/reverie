package io.github.aspasiax.reverie.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents the profile fields a user may change.
 *
 * <p>The username and email address are not editable here because they
 * identify the account, and the password is changed through a dedicated
 * operation that also verifies the current one.</p>
 */
@Schema(
        name = "UpdateUserRequest",
        description = "Profile fields the authenticated user may update."
)
public record UpdateUserRequest(

        @Schema(
                description = "Name displayed on the profile.",
                example = "Emma Carter",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 150
        )
        @NotBlank
        @Size(min = 2, max = 150)
        String displayName,

        @Schema(description = "Optional short biography.", maxLength = 500)
        @Size(max = 500)
        String bio,

        @Schema(description = "Optional URL of the profile image.", maxLength = 1024)
        @Size(max = 1024)
        String profileImageUrl
) {
}
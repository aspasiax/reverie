package io.github.aspasiax.reverie.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data required to change the authenticated user's password.
 *
 * <p>The current password is asked for even though the caller is already
 * signed in. A token proves who opened the session, not who is at the
 * keyboard now.</p>
 *
 * @param currentPassword the password in use, to prove the account is the caller's
 * @param newPassword     the password to replace it with
 */
@Schema(
        name = "ChangePasswordRequest",
        description = "Data required to change the authenticated user's password."
)
public record ChangePasswordRequest(

        @Schema(
                description = "The password currently in use.",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "password"
        )
        @NotBlank
        String currentPassword,

        @Schema(
                description = """
                        The new password. Must be at least eight characters and contain \
                        one lowercase letter, one uppercase letter, one digit and one \
                        special character.""",
                example = "Reverie123!",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 8,
                maxLength = 100,
                format = "password"
        )
        @NotBlank
        @Size(min = 8, max = 100)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).+$",
                message = "Password must contain at least one lowercase letter, one uppercase letter, one digit and one special character."
        )
        String newPassword
) {
}
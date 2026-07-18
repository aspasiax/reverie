package io.github.aspasiax.reverie.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the credentials required to authenticate a user.
 *
 * @param email    the user's email address
 * @param password the user's raw password
 */
@Schema(
        name = "LoginRequest",
        description = "Credentials required to authenticate a Reverie user."
)
public record LoginRequest(

        @Schema(
                description = "Registered email address of the user.",
                example = "admin@reverie.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Email
        String email,

        @Schema(
                description = "Raw account password.",
                example = "admin123",
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "password"
        )
        @NotBlank
        String password
) {
}
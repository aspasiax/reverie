package io.github.aspasiax.reverie.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents the data required to register a new user.
 *
 * @param username    the unique public username
 * @param email       the user's email address
 * @param password    the user's raw password
 * @param displayName the name displayed on the user's profile
 */
@Schema(
        name = "RegisterRequest",
        description = "Data required to register a new Reverie user."
)
public record RegisterRequest(

        @Schema(
                description = "Unique public username used by the account.",
                example = "emma.watches",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 3,
                maxLength = 50
        )
        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @Schema(
                description = "Unique email address associated with the account.",
                example = "new.user@reverie.com",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 255
        )
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @Schema(
                description = """
                        Account password. It must contain at least one lowercase letter,
                        one uppercase letter, one digit and one special character.
                        """,
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
        String password,

        @Schema(
                description = "Name displayed publicly on the user's profile.",
                example = "Emma Carter",
                requiredMode = Schema.RequiredMode.REQUIRED,
                minLength = 2,
                maxLength = 150
        )
        @NotBlank
        @Size(min = 2, max = 150)
        String displayName
) {
}
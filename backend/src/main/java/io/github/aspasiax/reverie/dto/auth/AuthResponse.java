package io.github.aspasiax.reverie.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the response returned after successful authentication.
 *
 * @param accessToken the generated JWT access token
 * @param tokenType   the token type, typically {@code Bearer}
 * @param expiresIn   the token lifetime in seconds
 */
@Schema(
        name = "AuthResponse",
        description = "Authentication information returned after a successful login or registration."
)
public record AuthResponse(

        @Schema(
                description = "JWT access token used to authenticate protected API requests.",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkByZXZlcmllLmNvbSJ9.example-signature"
        )
        String accessToken,

        @Schema(
                description = "Authentication scheme used with the access token.",
                example = "Bearer"
        )
        String tokenType,

        @Schema(
                description = "Token lifetime in seconds.",
                example = "3600",
                minimum = "1"
        )
        long expiresIn
) {
}
package io.github.aspasiax.reverie.dto.auth;

/**
 * Represents the response returned after successful authentication.
 *
 * @param accessToken the generated JWT access token
 * @param tokenType   the token type, typically {@code Bearer}
 * @param expiresIn   the token lifetime in seconds
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
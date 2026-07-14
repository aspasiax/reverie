package io.github.aspasiax.reverie.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Represents the data required to register a new user.
 *
 * @param username    the unique public username
 * @param email       the user's email address
 * @param password    the user's raw password
 * @param displayName the name displayed on the user's profile
 */
public record RegisterRequest(

        @NotBlank
        @Size(min = 3, max = 50)
        String username,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        @Size(min = 2, max = 150)
        String displayName
) {
}
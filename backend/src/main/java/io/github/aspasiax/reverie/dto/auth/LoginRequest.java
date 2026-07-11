package io.github.aspasiax.reverie.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the credentials required to authenticate a user.
 *
 * @param email    the user's email address
 * @param password the user's raw password
 */
public record LoginRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}
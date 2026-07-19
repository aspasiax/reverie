package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Thrown when a user with the specified UUID cannot be found.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Creates a new exception for a missing user.
     *
     * @param uuid the missing user's UUID
     */
    public UserNotFoundException(UUID uuid) {
        super("User with UUID " + uuid + " was not found.");
    }

}
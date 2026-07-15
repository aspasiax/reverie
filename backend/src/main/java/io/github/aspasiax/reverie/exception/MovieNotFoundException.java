package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Thrown when an active movie cannot be found using
 * the supplied public UUID.
 */
public class MovieNotFoundException extends RuntimeException {

    /**
     * Creates a movie-not-found exception for the given UUID.
     *
     * @param uuid the public movie identifier
     */
    public MovieNotFoundException(UUID uuid) {
        super("Movie with UUID " + uuid + " was not found.");
    }
}
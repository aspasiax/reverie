package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Thrown when an active genre cannot be found using
 * the supplied public UUID.
 */
public class GenreNotFoundException extends RuntimeException {

    /**
     * Creates a genre-not-found exception for the given UUID.
     *
     * @param uuid the public genre identifier
     */
    public GenreNotFoundException(UUID uuid) {
        super("Genre with UUID " + uuid + " was not found.");
    }
}
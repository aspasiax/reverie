package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested review does not exist
 * or has been soft deleted.
 */
public class ReviewNotFoundException extends RuntimeException {

    /**
     * Creates a new exception for a missing review.
     *
     * @param uuid the UUID of the requested review
     */
    public ReviewNotFoundException(UUID uuid) {
        super("Review with UUID " + uuid + " was not found.");
    }
}
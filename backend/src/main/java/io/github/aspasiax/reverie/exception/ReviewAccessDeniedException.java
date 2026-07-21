package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Thrown when a user attempts to modify or delete a review
 * that belongs to another user.
 */
public class ReviewAccessDeniedException extends RuntimeException {

    /**
     * Creates a new exception for an unauthorized review operation.
     *
     * @param reviewUuid the UUID of the review
     */
    public ReviewAccessDeniedException(UUID reviewUuid) {
        super("You are not allowed to modify review with UUID: " + reviewUuid);
    }
}
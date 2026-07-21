package io.github.aspasiax.reverie.exception;

/**
 * Exception thrown when a user attempts to create
 * more than one active review for the same movie.
 */
public class DuplicateReviewException extends IllegalArgumentException {

    /**
     * Creates a new duplicate review exception.
     */
    public DuplicateReviewException() {
        super("You have already created a review for this movie.");
    }
}
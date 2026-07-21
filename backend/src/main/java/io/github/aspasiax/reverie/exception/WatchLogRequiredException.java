package io.github.aspasiax.reverie.exception;

/**
 * Exception thrown when a user attempts to create
 * a review without an active watch log.
 */
public class WatchLogRequiredException extends RuntimeException {

    /**
     * Creates a new watch log required exception.
     */
    public WatchLogRequiredException() {
        super("You must have an active watch log before creating a review.");
    }
}
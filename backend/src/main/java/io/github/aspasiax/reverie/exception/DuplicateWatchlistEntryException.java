package io.github.aspasiax.reverie.exception;

/**
 * Thrown when a user adds a film that is already on their watchlist.
 *
 * <p>An entry records an intention, and an intention is held once. The
 * request conflicts with the current state rather than being malformed,
 * so it is reported as a conflict.</p>
 */
public class DuplicateWatchlistEntryException extends RuntimeException {

    /**
     * Creates a new duplicate watchlist entry exception.
     */
    public DuplicateWatchlistEntryException() {
        super("This film is already on your watchlist.");
    }
}
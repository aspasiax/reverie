package io.github.aspasiax.reverie.exception;

/**
 * Exception thrown when a user attempts something that
 * requires having watched the film first.
 */
public class WatchLogRequiredException extends RuntimeException {

    /**
     * Creates a new watch log required exception.
     *
     * @param action what the user was attempting, completing the sentence
     *               "You must have an active watch log before ..."
     */
    public WatchLogRequiredException(String action) {
        super("You must have an active watch log before " + action + ".");
    }
}
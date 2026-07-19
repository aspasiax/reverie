package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Thrown when a watch log with the specified UUID cannot be found.
 */
public class WatchLogNotFoundException extends RuntimeException {

    /**
     * Creates a new exception for a missing watch log.
     *
     * @param uuid the missing watch log UUID
     */
    public WatchLogNotFoundException(UUID uuid) {
        super("Watch log with UUID " + uuid + " was not found.");
    }

}
package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Thrown when a watchlist entry does not exist or has been removed.
 */
public class WatchlistEntryNotFoundException extends RuntimeException {

    /**
     * Creates a new watchlist entry not found exception.
     *
     * @param uuid the public identifier that was requested
     */
    public WatchlistEntryNotFoundException(UUID uuid) {
        super("Watchlist entry with UUID " + uuid + " was not found.");
    }
}
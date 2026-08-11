package io.github.aspasiax.reverie.mapper;

import io.github.aspasiax.reverie.domain.WatchlistEntry;
import io.github.aspasiax.reverie.dto.watchlist.WatchlistEntryResponse;
import org.springframework.stereotype.Component;

/**
 * Maps watchlist entries to API response DTOs.
 */
@Component
public class WatchlistEntryMapper {

    /**
     * Maps an entry to its API response.
     *
     * @param entry the entry entity
     * @return the entry as the API returns it
     */
    public WatchlistEntryResponse toResponse(WatchlistEntry entry) {
        return new WatchlistEntryResponse(
                entry.getUuid(),
                entry.getMovie().getUuid(),
                entry.getMovie().getTitle(),
                entry.getMovie().getPosterPath(),
                entry.getCreatedAt()
        );
    }
}
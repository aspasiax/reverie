package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.watchlist.CreateWatchlistEntryRequest;
import io.github.aspasiax.reverie.dto.watchlist.WatchlistEntryResponse;
import io.github.aspasiax.reverie.exception.DuplicateWatchlistEntryException;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import io.github.aspasiax.reverie.exception.WatchlistEntryNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

/**
 * Defines the operations available on the authenticated user's watchlist.
 *
 * <p>A watchlist is private to its owner. Every operation applies to the
 * account making the call, which is why no method takes a user.</p>
 */
public interface IWatchlistService {

    /**
     * Retrieves a page of the authenticated user's watchlist.
     *
     * @param pageable the requested page and sort order
     * @return a page of the films the user intends to watch
     */
    PageResponse<WatchlistEntryResponse> findMyWatchlist(Pageable pageable);

    /**
     * Adds a film to the authenticated user's watchlist.
     *
     * @param request the film to add
     * @return the created entry
     * @throws MovieNotFoundException              if the film does not exist
     * @throws DuplicateWatchlistEntryException    if it is already on the list
     */
    WatchlistEntryResponse add(CreateWatchlistEntryRequest request);

    /**
     * Removes an entry from the authenticated user's watchlist.
     *
     * @param uuid the public entry identifier
     * @throws WatchlistEntryNotFoundException if the entry does not exist
     * @throws AccessDeniedException           if it belongs to someone else
     */
    void remove(UUID uuid);

    /**
     * Removes the entry a user holds for a film, if there is one.
     *
     * <p>Called when a film is logged as watched. Adding a film to the
     * list states an intention, and watching it fulfils that intention,
     * so the entry has served its purpose.</p>
     *
     * @param userUuid  the public user identifier
     * @param movieUuid the public movie identifier
     */
    void removeForWatchedMovie(UUID userUuid, UUID movieUuid);
}
package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.watchlog.CreateWatchLogRequest;
import io.github.aspasiax.reverie.dto.watchlog.WatchLogResponse;

import java.util.List;
import java.util.UUID;

/**
 * Defines the business operations related to user watch logs.
 */
public interface IWatchLogService {

    /**
     * Retrieves the authenticated user's watch history.
     *
     * @return a list containing the user's watch logs
     */
    List<WatchLogResponse> findMyWatchLogs();

    /**
     * Creates a new watch log for the authenticated user.
     *
     * @param request the watch log creation request
     * @return the created watch log response
     */
    WatchLogResponse create(CreateWatchLogRequest request);

    /**
     * Deletes one of the authenticated user's watch logs.
     *
     * @param uuid the watch log UUID
     */
    void delete(UUID uuid);

}
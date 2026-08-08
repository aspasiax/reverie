package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.watchlog.CreateWatchLogRequest;
import io.github.aspasiax.reverie.dto.watchlog.UpdateWatchLogRequest;
import io.github.aspasiax.reverie.dto.watchlog.WatchLogResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Defines the business operations related to user watch logs.
 */
public interface IWatchLogService {

    /**
     * Retrieves the authenticated user's watch history.
     *
     * @param pageable the requested page and sort order
     * @return a list containing the user's watch logs
     */
    PageResponse<WatchLogResponse> findMyWatchLogs(Pageable pageable);

    /**
     * Creates a new watch log for the authenticated user.
     *
     * @param request the watch log creation request
     * @return the created watch log response
     */
    WatchLogResponse create(CreateWatchLogRequest request);

    /**
     * Corrects the viewing date of an existing watch log.
     *
     * @param uuid    the public watch log identifier
     * @param request the corrected values
     * @return the updated watch log
     */
    WatchLogResponse update(UUID uuid, UpdateWatchLogRequest request);

    /**
     * Deletes one of the authenticated user's watch logs.
     *
     * @param uuid the watch log UUID
     */
    void delete(UUID uuid);

}
package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.user.UserStatisticsResponse;
import io.github.aspasiax.reverie.exception.UserNotFoundException;

import java.util.UUID;

/**
 * Summarises what has happened in the application.
 *
 * <p>Everything here is read only and derived from records that already
 * exist. Nothing in this service creates or changes anything.</p>
 */
public interface IStatisticsService {

    /**
     * Summarises the public activity of a user.
     *
     * @param uuid the public user identifier
     * @return the user's activity in numbers
     * @throws UserNotFoundException if no such user exists
     */
    UserStatisticsResponse getUserStatistics(UUID uuid);
}
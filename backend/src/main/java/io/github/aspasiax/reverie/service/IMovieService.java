package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.movie.CreateMovieRequest;
import io.github.aspasiax.reverie.dto.movie.MovieResponse;
import io.github.aspasiax.reverie.dto.movie.UpdateMovieRequest;

import java.util.List;
import java.util.UUID;

/**
 * Defines the business operations related to movies.
 */
public interface IMovieService {

    /**
     * Retrieves all active movies.
     *
     * @return a list containing all active movies
     */
    List<MovieResponse> findAll();

    /**
     * Finds an active movie by its public UUID.
     *
     * @param uuid the movie UUID
     * @return the matching movie response
     */
    MovieResponse findByUuid(UUID uuid);

    /**
     * Creates a new movie.
     *
     * @param request the movie creation request
     * @return the created movie response
     */
    MovieResponse create(CreateMovieRequest request);

    /**
     * Updates an existing movie using its public UUID.
     *
     * @param uuid    the movie UUID
     * @param request the movie update request
     * @return the updated movie response
     */
    MovieResponse update(
            UUID uuid,
            UpdateMovieRequest request
    );

    /**
     * Soft deletes a movie using its public UUID.
     *
     * @param uuid the movie UUID
     */
    void delete(UUID uuid);

    /**
     * Checks whether a movie with the given TMDB identifier exists.
     *
     * @param tmdbId the TMDB movie identifier
     * @return {@code true} if such a movie exists
     */
    boolean existsByTmdbId(Long tmdbId);
}
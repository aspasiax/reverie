package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines business operations related to movies.
 */
public interface IMovieService {

    /**
     * Retrieves all movies currently stored in the system.
     *
     * @return a list of movies
     */
    List<Movie> findAll();

    /**
     * Finds a movie by its public UUID.
     *
     * @param uuid the movie UUID
     * @return the matching movie, if found
     */
    Optional<Movie> findByUuid(UUID uuid);

    /**
     * Checks whether a movie with the given TMDB identifier exists.
     *
     * @param tmdbId the TMDB movie identifier
     * @return {@code true} if such movie exists
     */
    boolean existsByTmdbId(Long tmdbId);
}
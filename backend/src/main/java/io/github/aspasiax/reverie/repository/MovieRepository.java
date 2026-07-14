package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to movies.
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Finds a movie by its public UUID.
     *
     * @param uuid the movie UUID
     * @return the matching movie, if found
     */
    Optional<Movie> findByUuid(UUID uuid);

    /**
     * Finds a movie by its unique TMDB identifier.
     *
     * @param tmdbId the TMDB movie identifier
     * @return the matching movie, if found
     */
    Optional<Movie> findByTmdbId(Long tmdbId);

    /**
     * Checks whether a movie with the given public UUID exists.
     *
     * @param uuid the movie UUID
     * @return {@code true} if a movie exists with the given UUID
     */
    boolean existsByUuid(UUID uuid);

    /**
     * Checks whether a movie with the given TMDB identifier exists.
     *
     * @param tmdbId the TMDB movie identifier
     * @return {@code true} if a movie exists with the given TMDB identifier
     */
    boolean existsByTmdbId(Long tmdbId);
}
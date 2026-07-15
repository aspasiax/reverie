package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to movies.
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Returns all movies that have not been soft deleted.
     *
     * @return a list containing all active movies
     */
    List<Movie> findAllByDeletedFalse();

    /**
     * Finds an active movie by its public UUID.
     *
     * @param uuid the movie UUID
     * @return the matching active movie, if found
     */
    Optional<Movie> findByUuidAndDeletedFalse(UUID uuid);

    /**
     * Finds a movie by its unique TMDB identifier.
     *
     * <p>This method also includes soft-deleted movies so that a TMDB
     * identifier cannot accidentally be reused for another record.</p>
     *
     * @param tmdbId the TMDB movie identifier
     * @return the matching movie, if found
     */
    Optional<Movie> findByTmdbId(Long tmdbId);

    /**
     * Finds a movie by its unique IMDb identifier.
     *
     * @param imdbId the IMDb movie identifier
     * @return the matching movie, if found
     */
    Optional<Movie> findByImdbId(String imdbId);

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
     * <p>Soft-deleted movies are included because the database
     * uniqueness constraint also includes those records.</p>
     *
     * @param tmdbId the TMDB movie identifier
     * @return {@code true} if a movie exists with the given TMDB identifier
     */
    boolean existsByTmdbId(Long tmdbId);
}
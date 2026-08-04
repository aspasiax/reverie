package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to movies.
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Returns a page of movies that have not been soft deleted.
     *
     * @param pageable the requested page and sort order
     * @return a page of active movies
     */
    Page<Movie> findAllByDeletedFalse(Pageable pageable);

    /**
     * Finds an active movie by its public UUID.
     *
     * @param uuid the movie UUID
     * @return the matching active movie, if found
     */
    Optional<Movie> findByUuidAndDeletedFalse(UUID uuid);

    /**
     * Finds a movie by its public UUID, including soft-deleted records.
     *
     * <p>Unlike {@link #findByUuidAndDeletedFalse(UUID)}, this lookup also
     * returns deleted movies, which is required in order to restore them.</p>
     *
     * @param uuid the movie UUID
     * @return the matching movie, if found
     */
    Optional<Movie> findByUuid(UUID uuid);

    /**
     * Finds an active movie by its unique TMDB identifier.
     *
     * <p>Soft-deleted movies are excluded because the database uniqueness
     * constraint only applies to active records. A TMDB identifier that
     * belonged to a deleted movie may therefore be assigned again.</p>
     *
     * @param tmdbId the TMDB movie identifier
     * @return the matching active movie, if found
     */
    Optional<Movie> findByTmdbIdAndDeletedFalse(Long tmdbId);

    /**
     * Finds an active movie by its unique IMDb identifier.
     *
     * <p>Soft-deleted movies are excluded, following the same rule as
     * the TMDB identifier lookup.</p>
     *
     * @param imdbId the IMDb movie identifier
     * @return the matching active movie, if found
     */
    Optional<Movie> findByImdbIdAndDeletedFalse(String imdbId);

    /**
     * Checks whether a movie with the given public UUID exists.
     *
     * @param uuid the movie UUID
     * @return {@code true} if a movie exists with the given UUID
     */
    boolean existsByUuid(UUID uuid);

    /**
     * Checks whether an active movie with the given TMDB identifier exists.
     *
     * <p>Soft-deleted movies are excluded because the database uniqueness
     * constraint only applies to active records.</p>
     *
     * @param tmdbId the TMDB movie identifier
     * @return {@code true} if an active movie uses the identifier
     */
    boolean existsByTmdbIdAndDeletedFalse(Long tmdbId);
}
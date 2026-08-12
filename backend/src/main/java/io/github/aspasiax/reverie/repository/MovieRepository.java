package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
     * Returns a page of published movies matching the given filters,
     * alphabetically by title.
     *
     * <p>Both filters are optional and are ignored when null, which is what
     * lets one query serve a plain catalogue and a filtered one. The title
     * is matched case insensitively on any part of it.</p>
     *
     * @param search    part of a title to match, empty for no filter
     * @param genreUuid the genre a film must carry, or null for no filter
     * @param pageable  the requested page, whose own sort order is ignored
     * @return a page of published movies
     */
    @Query(
            value = """
                    SELECT m FROM Movie m
                    WHERE m.deleted = FALSE AND m.published = TRUE
                      AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))
                      AND (:genreUuid IS NULL OR :genreUuid IN (SELECT g.uuid FROM m.genres g))
                    ORDER BY m.title ASC
                    """,
            countQuery = """
                    SELECT COUNT(m) FROM Movie m
                    WHERE m.deleted = FALSE AND m.published = TRUE
                      AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))
                      AND (:genreUuid IS NULL OR :genreUuid IN (SELECT g.uuid FROM m.genres g))
                    """
    )
    Page<Movie> findPublishedOrderedByTitle(
            @Param("search") String search,
            @Param("genreUuid") UUID genreUuid,
            Pageable pageable
    );

    /**
     * Returns a page of published movies matching the given filters, most
     * watched first.
     *
     * <p>The number of viewings is counted in a subquery rather than through
     * a join, so that a movie nobody has watched still appears, with a count
     * of zero, at the end of the list.</p>
     *
     * @param search    part of a title to match, empty for no filter
     * @param genreUuid the genre a film must carry, or null for no filter
     * @param pageable  the requested page, whose own sort order is ignored
     * @return a page of published movies ordered by recorded viewings
     */
    @Query(
            value = """
                    SELECT m FROM Movie m
                    WHERE m.deleted = FALSE AND m.published = TRUE
                      AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))
                      AND (:genreUuid IS NULL OR :genreUuid IN (SELECT g.uuid FROM m.genres g))
                    ORDER BY (
                        SELECT COUNT(w) FROM WatchLog w
                        WHERE w.movie = m AND w.deleted = FALSE
                    ) DESC, m.title ASC
                    """,
            countQuery = """
                    SELECT COUNT(m) FROM Movie m
                    WHERE m.deleted = FALSE AND m.published = TRUE
                      AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))
                      AND (:genreUuid IS NULL OR :genreUuid IN (SELECT g.uuid FROM m.genres g))
                    """
    )
    Page<Movie> findPublishedOrderedByViewings(
            @Param("search") String search,
            @Param("genreUuid") UUID genreUuid,
            Pageable pageable
    );

    /**
     * Returns a page of published movies matching the given filters, highest
     * rated first.
     *
     * <p>Ratings are optional on a review, and a movie may have no reviews
     * at all. Both cases produce no average, which is turned into zero so
     * that unrated movies sort last instead of disappearing or leading.</p>
     *
     * @param search    part of a title to match, empty for no filter
     * @param genreUuid the genre a film must carry, or null for no filter
     * @param pageable  the requested page, whose own sort order is ignored
     * @return a page of published movies ordered by average rating
     */
    @Query(
            value = """
                    SELECT m FROM Movie m
                    WHERE m.deleted = FALSE AND m.published = TRUE
                      AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))
                      AND (:genreUuid IS NULL OR :genreUuid IN (SELECT g.uuid FROM m.genres g))
                    ORDER BY COALESCE((
                        SELECT AVG(r.rating) FROM Review r
                        WHERE r.movie = m AND r.deleted = FALSE
                    ), 0) DESC, m.title ASC
                    """,
            countQuery = """
                    SELECT COUNT(m) FROM Movie m
                    WHERE m.deleted = FALSE AND m.published = TRUE
                      AND LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%'))
                      AND (:genreUuid IS NULL OR :genreUuid IN (SELECT g.uuid FROM m.genres g))
                    """
    )
    Page<Movie> findPublishedOrderedByRating(
            @Param("search") String search,
            @Param("genreUuid") UUID genreUuid,
            Pageable pageable
    );

    /**
     * Returns a page of movies that have been soft deleted.
     *
     * <p>This is the counterpart of {@link #findAllByDeletedFalse(Pageable)}
     * and exists so that deleted movies can be listed and restored.</p>
     *
     * @param pageable the requested page and sort order
     * @return a page of soft-deleted movies
     */
    Page<Movie> findAllByDeletedTrue(Pageable pageable);

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
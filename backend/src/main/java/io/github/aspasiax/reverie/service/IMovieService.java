package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.movie.CreateMovieRequest;
import io.github.aspasiax.reverie.dto.movie.MovieResponse;
import io.github.aspasiax.reverie.dto.movie.UpdateMovieRequest;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Defines the business operations related to movies.
 */
public interface IMovieService {

    /**
     * Retrieves a page of published movies.
     *
     * <p>This is the catalogue as readers see it. Movies that exist but have
     * not been published yet are excluded.</p>
     *
     * @param pageable the requested page and sort order
     * @return a page of published movies
     */
    PageResponse<MovieResponse> findAllPublished(Pageable pageable);

    /**
     * Retrieves a page of every movie that has not been deleted.
     *
     * <p>Unlike {@link #findAllPublished(Pageable)}, this listing also
     * returns unpublished movies, which is what makes them reachable for
     * administration.</p>
     *
     * @param pageable the requested page and sort order
     * @return a page of active movies, published or not
     */
    PageResponse<MovieResponse> findAllActive(Pageable pageable);

    /**
     * Retrieves a page of soft-deleted movies.
     *
     * @param pageable the requested page and sort order
     * @return a page of soft-deleted movies
     */
    PageResponse<MovieResponse> findAllDeleted(Pageable pageable);
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
     * Restores a previously soft-deleted movie.
     *
     * @param uuid the public movie identifier
     * @return the restored movie
     */
    MovieResponse restore(UUID uuid);

    /**
     * Publishes a movie, making it visible in the catalogue.
     *
     * @param uuid the public movie identifier
     * @return the published movie
     * @throws MovieNotFoundException if no active movie exists
     */
    MovieResponse publish(UUID uuid);

    /**
     * Withdraws a movie from the catalogue without deleting it.
     *
     * @param uuid the public movie identifier
     * @return the unpublished movie
     * @throws MovieNotFoundException if no active movie exists
     */
    MovieResponse unpublish(UUID uuid);

    /**
     * Checks whether a movie with the given TMDB identifier exists.
     *
     * @param tmdbId the TMDB movie identifier
     * @return {@code true} if such a movie exists
     */
    boolean existsByTmdbId(Long tmdbId);
}
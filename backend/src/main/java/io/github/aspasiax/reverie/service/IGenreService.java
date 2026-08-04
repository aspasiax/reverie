package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.genre.CreateGenreRequest;
import io.github.aspasiax.reverie.dto.genre.GenreResponse;
import io.github.aspasiax.reverie.dto.genre.UpdateGenreRequest;

import java.util.List;
import java.util.UUID;

/**
 * Defines the business operations related to movie genres.
 */
public interface IGenreService {

    /**
     * Retrieves all active genres.
     *
     * @return a list containing all active genres
     */
    List<GenreResponse> findAll();

    /**
     * Finds an active genre by its public UUID.
     *
     * @param uuid the genre UUID
     * @return the matching genre response
     */
    GenreResponse findByUuid(UUID uuid);

    /**
     * Creates a new genre.
     *
     * @param request the genre creation request
     * @return the created genre response
     */
    GenreResponse create(CreateGenreRequest request);

    /**
     * Updates an existing genre using its public UUID.
     *
     * @param uuid    the genre UUID
     * @param request the genre update request
     * @return the updated genre response
     */
    GenreResponse update(
            UUID uuid,
            UpdateGenreRequest request
    );

    /**
     * Soft deletes a genre using its public UUID.
     *
     * @param uuid the genre UUID
     */
    void delete(UUID uuid);

    /**
     * Restores a previously soft-deleted genre.
     *
     * @param uuid the public genre identifier
     * @return the restored genre
     */
    GenreResponse restore(UUID uuid);
}
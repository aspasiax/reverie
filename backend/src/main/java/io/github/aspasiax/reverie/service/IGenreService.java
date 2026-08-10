package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.genre.CreateGenreRequest;
import io.github.aspasiax.reverie.dto.genre.GenreResponse;
import io.github.aspasiax.reverie.dto.genre.UpdateGenreRequest;
import io.github.aspasiax.reverie.exception.DuplicateGenreNameException;
import io.github.aspasiax.reverie.exception.GenreNotFoundException;
import io.github.aspasiax.reverie.exception.PermanentDeleteNotApplicableException;
import io.github.aspasiax.reverie.exception.RestoreNotApplicableException;

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
     * Returns all soft-deleted genres in alphabetical order.
     *
     * <p>Deleted genres are hidden from the catalogue, so this listing is
     * the only way to reach a genre in order to restore it.</p>
     *
     * @return a list containing all soft-deleted genres
     */
    List<GenreResponse> findAllDeleted();

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
     * <p>A genre can only return to the catalogue if its name is still
     * free, because active genre names are unique.</p>
     *
     * @param uuid the public genre identifier
     * @return the restored genre
     * @throws GenreNotFoundException       if no genre exists
     * @throws RestoreNotApplicableException if the genre is not deleted
     * @throws DuplicateGenreNameException  if the name is taken by an active genre
     */
    GenreResponse restore(UUID uuid);

    /**
     * Permanently removes a genre from the database.
     *
     * <p>Only a genre that has already been soft deleted may be destroyed,
     * so that an irreversible removal always takes two deliberate steps.
     * The links to the films that carried the genre are removed with it.</p>
     *
     * @param uuid the public genre identifier
     * @throws GenreNotFoundException                  if no genre exists
     * @throws PermanentDeleteNotApplicableException   if the genre is still active
     */
    void deletePermanently(UUID uuid);
}
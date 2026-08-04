package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Genre;
import io.github.aspasiax.reverie.dto.genre.CreateGenreRequest;
import io.github.aspasiax.reverie.dto.genre.GenreResponse;
import io.github.aspasiax.reverie.dto.genre.UpdateGenreRequest;
import io.github.aspasiax.reverie.exception.DuplicateGenreNameException;
import io.github.aspasiax.reverie.exception.GenreNotFoundException;
import io.github.aspasiax.reverie.exception.RestoreNotApplicableException;
import io.github.aspasiax.reverie.mapper.GenreMapper;
import io.github.aspasiax.reverie.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link IGenreService}.
 *
 * <p>Handles genre creation, retrieval, updating and soft deletion.
 * It also enforces case-insensitive genre name uniqueness and maps
 * genre entities to API response DTOs.</p>
 */
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements IGenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<GenreResponse> findAll() {
        return genreRepository.findAllByDeletedFalseOrderByNameAsc()
                .stream()
                .map(genreMapper::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public GenreResponse findByUuid(UUID uuid) {
        Genre genre = findActiveGenre(uuid);

        return genreMapper.toResponse(genre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public GenreResponse create(CreateGenreRequest request) {
        String normalizedName = request.name().trim();

        validateUniqueName(normalizedName, null);

        Genre genre = genreMapper.toEntity(request);
        Genre savedGenre = genreRepository.save(genre);

        return genreMapper.toResponse(savedGenre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public GenreResponse update(
            UUID uuid,
            UpdateGenreRequest request
    ) {
        Genre genre = findActiveGenre(uuid);
        String normalizedName = request.name().trim();

        validateUniqueName(normalizedName, genre.getId());

        genreMapper.updateEntity(genre, request);

        Genre updatedGenre = genreRepository.save(genre);

        return genreMapper.toResponse(updatedGenre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(UUID uuid) {
        Genre genre = findActiveGenre(uuid);

        genre.softDelete();
        genreRepository.save(genre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public GenreResponse restore(UUID uuid) {
        Genre genre = genreRepository.findByUuid(uuid)
                .orElseThrow(() -> new GenreNotFoundException(uuid));

        if (!genre.isDeleted()) {
            throw new RestoreNotApplicableException("Genre", uuid);
        }

        genre.restoreFromSoftDelete();

        Genre restoredGenre = genreRepository.save(genre);

        return genreMapper.toResponse(restoredGenre);
    }

    /**
     * Finds a genre that has not been soft deleted.
     *
     * @param uuid the public genre identifier
     * @return the active genre entity
     * @throws GenreNotFoundException if no active genre exists
     */
    private Genre findActiveGenre(UUID uuid) {
        return genreRepository.findByUuidAndDeletedFalse(uuid)
                .orElseThrow(() -> new GenreNotFoundException(uuid));
    }

    /**
     * Ensures that a genre name is not assigned to another genre.
     *
     * <p>Name comparison is case-insensitive. During an update,
     * the current genre is excluded from duplicate detection.</p>
     *
     * @param name           the normalized genre name
     * @param currentGenreId the current genre id during update,
     *                       or {@code null} during creation
     * @throws DuplicateGenreNameException if the name is already used
     */
    private void validateUniqueName(
            String name,
            Long currentGenreId
    ) {
        genreRepository.findByNameIgnoreCaseAndDeletedFalse(name)
                .filter(genre -> !genre.getId().equals(currentGenreId))
                .ifPresent(genre -> {
                    throw new DuplicateGenreNameException(name);
                });
    }
}
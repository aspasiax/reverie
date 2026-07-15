package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Genre;
import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.dto.movie.CreateMovieRequest;
import io.github.aspasiax.reverie.dto.movie.MovieResponse;
import io.github.aspasiax.reverie.dto.movie.UpdateMovieRequest;
import io.github.aspasiax.reverie.exception.DuplicateMovieIdentifierException;
import io.github.aspasiax.reverie.exception.GenreNotFoundException;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import io.github.aspasiax.reverie.mapper.MovieMapper;
import io.github.aspasiax.reverie.repository.GenreRepository;
import io.github.aspasiax.reverie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Default implementation of {@link IMovieService}.
 *
 * <p>Handles movie creation, retrieval, updating and soft deletion.
 * It also validates external identifiers, resolves genre associations
 * and maps movie entities to API response DTOs.</p>
 */
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements IMovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final MovieMapper movieMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<MovieResponse> findAll() {
        return movieRepository.findAllByDeletedFalse()
                .stream()
                .map(movieMapper::toResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public MovieResponse findByUuid(UUID uuid) {
        Movie movie = findActiveMovie(uuid);

        return movieMapper.toResponse(movie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MovieResponse create(CreateMovieRequest request) {

        validateExternalIdentifiers(
                request.tmdbId(),
                request.imdbId(),
                null
        );

        Movie movie = movieMapper.toEntity(request);

        Set<Genre> genres = resolveGenres(request.genreUuids());
        replaceGenres(movie, genres);

        Movie savedMovie = movieRepository.save(movie);

        return movieMapper.toResponse(savedMovie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MovieResponse update(
            UUID uuid,
            UpdateMovieRequest request
    ) {
        Movie movie = findActiveMovie(uuid);

        validateExternalIdentifiers(
                request.tmdbId(),
                request.imdbId(),
                movie.getId()
        );

        movieMapper.updateEntity(movie, request);

        Set<Genre> genres = resolveGenres(request.genreUuids());
        replaceGenres(movie, genres);

        Movie updatedMovie = movieRepository.save(movie);

        return movieMapper.toResponse(updatedMovie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(UUID uuid) {
        Movie movie = findActiveMovie(uuid);

        movie.softDelete();
        movieRepository.save(movie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTmdbId(Long tmdbId) {
        return tmdbId != null
                && movieRepository.existsByTmdbId(tmdbId);
    }

    /**
     * Finds a movie that has not been soft deleted.
     *
     * @param uuid the public movie identifier
     * @return the active movie entity
     * @throws MovieNotFoundException if no active movie exists
     */
    private Movie findActiveMovie(UUID uuid) {
        return movieRepository.findByUuidAndDeletedFalse(uuid)
                .orElseThrow(() -> new MovieNotFoundException(uuid));
    }

    /**
     * Resolves genre UUIDs to active genre entities.
     *
     * <p>A missing or empty set produces a movie without genres.
     * Soft-deleted genres cannot be assigned to a movie.</p>
     *
     * @param genreUuids the genre UUIDs supplied by the request
     * @return the resolved genre entities
     * @throws GenreNotFoundException if a genre does not exist
     */
    private Set<Genre> resolveGenres(Set<UUID> genreUuids) {
        if (genreUuids == null || genreUuids.isEmpty()) {
            return Set.of();
        }

        Set<Genre> genres = new HashSet<>();

        for (UUID genreUuid : genreUuids) {
            Genre genre = genreRepository
                    .findByUuidAndDeletedFalse(genreUuid)
                    .orElseThrow(() -> new GenreNotFoundException(genreUuid));

            genres.add(genre);
        }

        return genres;
    }

    /**
     * Replaces all genre associations of a movie while keeping both
     * sides of the bidirectional relationship synchronized.
     *
     * @param movie  the movie whose genres will be replaced
     * @param genres the new genre set
     */
    private void replaceGenres(
            Movie movie,
            Set<Genre> genres
    ) {
        Set<Genre> existingGenres =
                new HashSet<>(movie.getGenres());

        for (Genre existingGenre : existingGenres) {
            movie.removeGenre(existingGenre);
        }

        for (Genre genre : genres) {
            movie.addGenre(genre);
        }
    }

    /**
     * Ensures that optional TMDB and IMDb identifiers are not assigned
     * to another movie.
     *
     * <p>When updating a movie, its existing database identifier is
     * excluded from duplicate detection.</p>
     *
     * @param tmdbId         the optional TMDB identifier
     * @param imdbId         the optional IMDb identifier
     * @param currentMovieId the current movie id during update,
     *                       or {@code null} during creation
     */
    private void validateExternalIdentifiers(
            Long tmdbId,
            String imdbId,
            Long currentMovieId
    ) {
        if (tmdbId != null) {
            movieRepository.findByTmdbId(tmdbId)
                    .filter(movie -> !movie.getId().equals(currentMovieId))
                    .ifPresent(movie -> {
                        throw new DuplicateMovieIdentifierException(
                                "TMDB",
                                tmdbId
                        );
                    });
        }

        String normalizedImdbId = normalizeOptionalValue(imdbId);

        if (normalizedImdbId != null) {
            movieRepository.findByImdbId(normalizedImdbId)
                    .filter(movie -> !movie.getId().equals(currentMovieId))
                    .ifPresent(movie -> {
                        throw new DuplicateMovieIdentifierException(
                                "IMDb",
                                normalizedImdbId
                        );
                    });
        }
    }

    /**
     * Trims an optional string and converts blank values to
     * {@code null}.
     *
     * @param value the value to normalize
     * @return the trimmed value or {@code null} when blank
     */
    private String normalizeOptionalValue(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Genre;
import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.movie.CreateMovieRequest;
import io.github.aspasiax.reverie.dto.movie.MovieResponse;
import io.github.aspasiax.reverie.dto.movie.MovieSort;
import io.github.aspasiax.reverie.dto.movie.UpdateMovieRequest;
import io.github.aspasiax.reverie.exception.DuplicateMovieIdentifierException;
import io.github.aspasiax.reverie.exception.GenreNotFoundException;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import io.github.aspasiax.reverie.exception.RestoreNotApplicableException;
import io.github.aspasiax.reverie.mapper.MovieMapper;
import io.github.aspasiax.reverie.repository.GenreRepository;
import io.github.aspasiax.reverie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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
    public PageResponse<MovieResponse> findAllPublished(
            Pageable pageable,
            MovieSort order,
            String search,
            UUID genreUuid
    ) {
        String normalizedSearch = normalizeSearch(search);
        Pageable page = withoutSort(pageable);

        Page<Movie> movies = switch (order) {
            case TITLE -> movieRepository
                    .findPublishedOrderedByTitle(normalizedSearch, genreUuid, page);
            case MOST_WATCHED -> movieRepository
                    .findPublishedOrderedByViewings(normalizedSearch, genreUuid, page);
            case TOP_RATED -> movieRepository
                    .findPublishedOrderedByRating(normalizedSearch, genreUuid, page);
        };

        return PageResponse.from(movies.map(movieMapper::toResponse));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> findAllActive(Pageable pageable) {
        Page<MovieResponse> page = movieRepository
                .findAllByDeletedFalse(pageable)
                .map(movieMapper::toResponse);

        return PageResponse.from(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MovieResponse> findAllDeleted(Pageable pageable) {
        Page<MovieResponse> page = movieRepository
                .findAllByDeletedTrue(pageable)
                .map(movieMapper::toResponse);

        return PageResponse.from(page);
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
    @Transactional
    public MovieResponse restore(UUID uuid) {
        Movie movie = movieRepository.findByUuid(uuid)
                .orElseThrow(() -> new MovieNotFoundException(uuid));

        if (!movie.isDeleted()) {
            throw new RestoreNotApplicableException("Movie", uuid);
        }

        movie.restoreFromSoftDelete();

        Movie restoredMovie = movieRepository.save(movie);

        return movieMapper.toResponse(restoredMovie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MovieResponse publish(UUID uuid) {
        Movie movie = findActiveMovie(uuid);

        movie.publish();

        return movieMapper.toResponse(movieRepository.save(movie));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public MovieResponse unpublish(UUID uuid) {
        Movie movie = findActiveMovie(uuid);

        movie.unpublish();

        return movieMapper.toResponse(movieRepository.save(movie));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTmdbId(Long tmdbId) {
        return tmdbId != null
                && movieRepository.existsByTmdbIdAndDeletedFalse(tmdbId);
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
     * Strips the sort order from a page request.
     *
     * <p>The computed orders are written into the query itself. Leaving the
     * incoming sort in place would make Spring Data append a second ordering
     * after it, which at best repeats what the query already does.</p>
     *
     * @param pageable the incoming page request
     * @return the same page and size, without any sort order
     */
    private Pageable withoutSort(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    /**
     * Turns a missing or blank search term into one that matches everything.
     *
     * <p>An empty box and an absent parameter mean the same thing to the
     * reader, so they are made to mean the same thing to the query. The
     * empty term is deliberate rather than null: PostgreSQL cannot infer a
     * type for an untyped null inside a string concatenation, and picks one
     * that has no {@code lower} function.</p>
     *
     * @param search the term as it arrived
     * @return the trimmed term, or an empty string when there is none
     */
    private String normalizeSearch(String search) {
        return search == null ? "" : search.trim();
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
            movieRepository.findByTmdbIdAndDeletedFalse(tmdbId)
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
            movieRepository.findByImdbIdAndDeletedFalse(normalizedImdbId)
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
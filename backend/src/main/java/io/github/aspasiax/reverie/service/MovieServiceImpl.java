package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link IMovieService}.
 *
 * <p>Contains business logic related to movie retrieval and existence checks.</p>
 */
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements IMovieService {

    private final MovieRepository movieRepository;

    /**
     * Retrieves all movies stored in the database.
     *
     * @return a list of movies
     */
    @Override
    @Transactional(readOnly = true)
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    /**
     * Finds a movie by its public UUID.
     *
     * @param uuid the movie UUID
     * @return the matching movie, if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Movie> findByUuid(UUID uuid) {
        return movieRepository.findByUuid(uuid);
    }

    /**
     * Checks whether a movie with the given TMDB identifier already exists.
     *
     * @param tmdbId the TMDB movie identifier
     * @return {@code true} if a movie exists with the given TMDB id
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTmdbId(Long tmdbId) {
        return movieRepository.existsByTmdbId(tmdbId);
    }
}
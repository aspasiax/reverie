package io.github.aspasiax.reverie.mapper;

import io.github.aspasiax.reverie.domain.Genre;
import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.dto.genre.GenreSummaryResponse;
import io.github.aspasiax.reverie.dto.movie.CreateMovieRequest;
import io.github.aspasiax.reverie.dto.movie.MovieResponse;
import io.github.aspasiax.reverie.dto.movie.UpdateMovieRequest;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Maps movie entities to API response DTOs and applies movie
 * request DTOs to domain entities.
 */
@Component
public class MovieMapper {

    /**
     * Creates a new movie entity from a movie creation request.
     *
     * <p>Genre associations are handled separately by the service
     * because they must be loaded from the database using their UUIDs.</p>
     *
     * @param request the movie creation request
     * @return a new movie entity
     */
    public Movie toEntity(CreateMovieRequest request) {
        return Movie.builder()
                .title(request.title().trim())
                .originalTitle(trimToNull(request.originalTitle()))
                .overview(trimToNull(request.overview()))
                .releaseDate(request.releaseDate())
                .runtime(request.runtime())
                .originalLanguage(normalizeLanguage(request.originalLanguage()))
                .posterPath(trimToNull(request.posterPath()))
                .backdropPath(trimToNull(request.backdropPath()))
                .tmdbId(request.tmdbId())
                .imdbId(trimToNull(request.imdbId()))
                .published(request.published())
                .build();
    }

    /**
     * Applies the values of an update request to an existing movie.
     *
     * <p>The movie UUID, database identifier, auditing fields and genre
     * associations are not modified by this method.</p>
     *
     * @param movie   the existing movie entity
     * @param request the movie update request
     */
    public void updateEntity(Movie movie, UpdateMovieRequest request) {
        movie.setTitle(request.title().trim());
        movie.setOriginalTitle(trimToNull(request.originalTitle()));
        movie.setOverview(trimToNull(request.overview()));
        movie.setReleaseDate(request.releaseDate());
        movie.setRuntime(request.runtime());
        movie.setOriginalLanguage(
                normalizeLanguage(request.originalLanguage())
        );
        movie.setPosterPath(trimToNull(request.posterPath()));
        movie.setBackdropPath(trimToNull(request.backdropPath()));
        movie.setTmdbId(request.tmdbId());
        movie.setImdbId(trimToNull(request.imdbId()));
        movie.setPublished(request.published());
    }

    /**
     * Maps a movie entity to the complete API response.
     *
     * @param movie the movie entity
     * @return the movie response
     */
    public MovieResponse toResponse(Movie movie) {
        return new MovieResponse(
                movie.getUuid(),
                movie.getTitle(),
                movie.getOriginalTitle(),
                movie.getOverview(),
                movie.getReleaseDate(),
                movie.getRuntime(),
                movie.getOriginalLanguage(),
                movie.getPosterPath(),
                movie.getBackdropPath(),
                movie.getTmdbId(),
                movie.getImdbId(),
                movie.isPublished(),
                movie.getAverageRating(),
                movie.getRatingCount(),
                movie.getWatchCount(),
                toGenreSummaries(movie.getGenres()),
                movie.getCreatedAt(),
                movie.getUpdatedAt()
        );
    }

    /**
     * Maps a set of genres to compact genre responses.
     *
     * <p>The genres are returned alphabetically to provide stable
     * ordering in API responses.</p>
     *
     * @param genres the movie genres
     * @return the compact genre responses
     */
    private Set<GenreSummaryResponse> toGenreSummaries(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Set.of();
        }

        return genres.stream()
                .sorted(Comparator.comparing(
                        Genre::getName,
                        String.CASE_INSENSITIVE_ORDER
                ))
                .map(this::toGenreSummary)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Maps a genre entity to its compact response representation.
     *
     * @param genre the genre entity
     * @return the compact genre response
     */
    private GenreSummaryResponse toGenreSummary(Genre genre) {
        return new GenreSummaryResponse(
                genre.getUuid(),
                genre.getName(),
                genre.getIcon(),
                genre.getColor()
        );
    }

    /**
     * Trims a string and converts blank values to {@code null}.
     *
     * @param value the value to normalize
     * @return the trimmed value or {@code null} when blank
     */
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    /**
     * Normalizes an optional language code to lowercase.
     *
     * @param language the language code
     * @return the normalized language code or {@code null}
     */
    private String normalizeLanguage(String language) {
        String normalizedLanguage = trimToNull(language);

        return normalizedLanguage == null
                ? null
                : normalizedLanguage.toLowerCase();
    }
}
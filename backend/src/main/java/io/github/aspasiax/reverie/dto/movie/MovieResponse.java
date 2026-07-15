package io.github.aspasiax.reverie.dto.movie;

import io.github.aspasiax.reverie.dto.genre.GenreSummaryResponse;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the complete movie information returned by the API.
 *
 * <p>This response is used when retrieving one or more movies.
 * It contains all information required by the frontend,
 * including the assigned genres.</p>
 *
 * @param uuid               the public movie identifier
 * @param title              the main movie title
 * @param originalTitle      the original movie title
 * @param overview           the movie plot summary
 * @param releaseDate        the official release date
 * @param runtime            the runtime in minutes
 * @param originalLanguage   the original language code
 * @param posterPath         the relative poster image path
 * @param backdropPath       the relative backdrop image path
 * @param tmdbId             the optional TMDB identifier
 * @param imdbId             the optional IMDb identifier
 * @param published          whether the movie is publicly visible
 * @param genres             the assigned movie genres
 * @param createdAt          the creation timestamp
 * @param updatedAt          the last update timestamp
 */
public record MovieResponse(

        UUID uuid,

        String title,

        String originalTitle,

        String overview,

        LocalDate releaseDate,

        Integer runtime,

        String originalLanguage,

        String posterPath,

        String backdropPath,

        Long tmdbId,

        String imdbId,

        boolean published,

        Set<GenreSummaryResponse> genres,

        Instant createdAt,

        Instant updatedAt
) {
}
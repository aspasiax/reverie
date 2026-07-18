package io.github.aspasiax.reverie.dto.movie;

import io.github.aspasiax.reverie.dto.genre.GenreSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
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
@Schema(
        name = "MovieResponse",
        description = "Complete movie information returned by the Reverie API."
)
public record MovieResponse(

        @Schema(
                description = "Public UUID of the movie.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID uuid,

        @Schema(
                description = "Main movie title.",
                example = "Interstellar"
        )
        String title,

        @Schema(
                description = "Original movie title.",
                example = "Interstellar"
        )
        String originalTitle,

        @Schema(
                description = "Movie plot summary.",
                example = "A team of explorers travels through a wormhole in space in an attempt to ensure humanity's survival."
        )
        String overview,

        @Schema(
                description = "Official movie release date.",
                example = "2014-11-07",
                type = "string",
                format = "date"
        )
        LocalDate releaseDate,

        @Schema(
                description = "Movie runtime in minutes.",
                example = "169"
        )
        Integer runtime,

        @Schema(
                description = "Original language code.",
                example = "en"
        )
        String originalLanguage,

        @Schema(
                description = "Relative path of the movie poster image.",
                example = "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"
        )
        String posterPath,

        @Schema(
                description = "Relative path of the movie backdrop image.",
                example = "/xJHokMbljvjADYdit5fK5VQsXEG.jpg"
        )
        String backdropPath,

        @Schema(
                description = "TMDB identifier.",
                example = "157336"
        )
        Long tmdbId,

        @Schema(
                description = "IMDb identifier.",
                example = "tt0816692"
        )
        String imdbId,

        @Schema(
                description = "Indicates whether the movie is publicly visible.",
                example = "true"
        )
        boolean published,

        @Schema(
                description = "Genres assigned to the movie."
        )
        Set<GenreSummaryResponse> genres,

        @Schema(
                description = "Timestamp when the movie was created.",
                example = "2024-01-15T10:30:00Z",
                type = "string",
                format = "date-time"
        )
        Instant createdAt,

        @Schema(
                description = "Timestamp of the last movie update.",
                example = "2024-03-02T16:45:18Z",
                type = "string",
                format = "date-time"
        )
        Instant updatedAt
) {
}
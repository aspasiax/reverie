package io.github.aspasiax.reverie.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a compact view of a movie.
 *
 * <p>This response is intended for embedding inside larger API responses,
 * such as the film a user names as their favourite. It carries only what a
 * poster and a link to the film need, so that mentioning a film elsewhere
 * never drags its ratings, genres and counts along with it.</p>
 *
 * @param uuid        the public movie identifier
 * @param title       the movie title
 * @param releaseDate the date the movie was released
 * @param posterPath  the poster path used to build the image address
 */
@Schema(
        name = "MovieSummaryResponse",
        description = "Compact movie information embedded in larger API responses."
)
public record MovieSummaryResponse(

        @Schema(
                description = "Public UUID of the movie.",
                example = "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
        )
        UUID uuid,

        @Schema(
                description = "Movie title.",
                example = "Arrival"
        )
        String title,

        @Schema(
                description = "Date the movie was released.",
                example = "2016-11-11"
        )
        LocalDate releaseDate,

        @Schema(
                description = "Poster path used to build the image address.",
                example = "/x2FJsf1ElAgr63Y3PNPtJrcmpoe.jpg"
        )
        String posterPath
) {
}
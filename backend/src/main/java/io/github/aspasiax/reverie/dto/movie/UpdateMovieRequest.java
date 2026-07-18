package io.github.aspasiax.reverie.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Represents the data required to update an existing movie.
 *
 * @param title            the main movie title
 * @param originalTitle    the original title in the movie's original language
 * @param overview         the movie plot summary
 * @param releaseDate      the official release date
 * @param runtime          the runtime in minutes
 * @param originalLanguage the original language code
 * @param posterPath       the relative poster image path
 * @param backdropPath     the relative backdrop image path
 * @param tmdbId           the optional TMDB identifier
 * @param imdbId           the optional IMDb identifier
 * @param published        whether the movie should be publicly visible
 * @param genreUuids       the UUIDs of the genres assigned to the movie
 */
@Schema(
        name = "UpdateMovieRequest",
        description = "Data required to update an existing movie in the Reverie catalog."
)
public record UpdateMovieRequest(

        @Schema(
                description = "Main movie title.",
                example = "Interstellar",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Size(max = 255)
        String title,

        @Schema(
                description = "Original movie title in its original language.",
                example = "Interstellar"
        )
        @Size(max = 255)
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
                example = "169",
                minimum = "1"
        )
        @Positive
        Integer runtime,

        @Schema(
                description = "Original language code of the movie.",
                example = "en",
                maxLength = 10
        )
        @Size(max = 10)
        String originalLanguage,

        @Schema(
                description = "Relative path of the movie poster image.",
                example = "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
                maxLength = 1024
        )
        @Size(max = 1024)
        String posterPath,

        @Schema(
                description = "Relative path of the movie backdrop image.",
                example = "/xJHokMbljvjADYdit5fK5VQsXEG.jpg",
                maxLength = 1024
        )
        @Size(max = 1024)
        String backdropPath,

        @Schema(
                description = "TMDB identifier of the movie.",
                example = "157336",
                minimum = "1"
        )
        @Positive
        Long tmdbId,

        @Schema(
                description = "IMDb identifier of the movie.",
                example = "tt0816692",
                maxLength = 20
        )
        @Size(max = 20)
        String imdbId,

        @Schema(
                description = "Indicates whether the movie is publicly visible.",
                example = "true"
        )
        boolean published,

        @Schema(
                description = "UUIDs of the genres assigned to the movie.",
                example = """
                        [
                          "550e8400-e29b-41d4-a716-446655440000",
                          "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
                        ]
                        """
        )
        Set<UUID> genreUuids
) {
}
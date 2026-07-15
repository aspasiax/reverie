package io.github.aspasiax.reverie.dto.movie;

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
public record UpdateMovieRequest(

        @NotBlank
        @Size(max = 255)
        String title,

        @Size(max = 255)
        String originalTitle,

        String overview,

        LocalDate releaseDate,

        @Positive
        Integer runtime,

        @Size(max = 10)
        String originalLanguage,

        @Size(max = 1024)
        String posterPath,

        @Size(max = 1024)
        String backdropPath,

        @Positive
        Long tmdbId,

        @Size(max = 20)
        String imdbId,

        boolean published,

        Set<UUID> genreUuids
) {
}
package io.github.aspasiax.reverie.dto.watchlist;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A film on the authenticated user's watchlist.
 *
 * <p>The film is described by the few fields a list needs to show it.
 * Anything more is one request away, through the film itself.</p>
 *
 * @param uuid       public identifier of the entry
 * @param movieUuid  public identifier of the film
 * @param movieTitle title of the film
 * @param releaseDate when the film was released, if known
 * @param posterPath relative TMDB poster path, if the film has one
 * @param createdAt  when the film was added to the list
 */
@Schema(
        name = "WatchlistEntryResponse",
        description = "A film the authenticated user intends to watch."
)
public record WatchlistEntryResponse(

        @Schema(description = "Public UUID of the entry.")
        UUID uuid,

        @Schema(description = "Public UUID of the film.")
        UUID movieUuid,

        @Schema(description = "Title of the film.", example = "Arrival")
        String movieTitle,

        @Schema(
                description = "Release date of the film.",
                type = "string",
                format = "date"
        )
        LocalDate releaseDate,

        @Schema(description = "Relative TMDB poster path.", example = "/x2FJsf1ElAgr63Y3PNPtJrcmpoe.jpg")
        String posterPath,

        @Schema(
                description = "When the film was added to the list.",
                type = "string",
                format = "date-time"
        )
        Instant createdAt
) {
}
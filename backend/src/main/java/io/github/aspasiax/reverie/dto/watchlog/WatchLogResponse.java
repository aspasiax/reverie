package io.github.aspasiax.reverie.dto.watchlog;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a movie viewing recorded in a user's watch history.
 *
 * @param uuid         the public watch log identifier
 * @param movieUuid    the public movie identifier
 * @param movieTitle   the title of the watched movie
 * @param posterPath   the relative movie poster path
 * @param watchedAt    the date on which the movie was watched
 * @param createdAt    the timestamp when the watch log was created
 */
@Schema(
        name = "WatchLogResponse",
        description = "A movie viewing recorded in the authenticated user's watch history."
)
public record WatchLogResponse(

        @Schema(
                description = "Public UUID of the watch log.",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID uuid,

        @Schema(
                description = "Public UUID of the watched movie.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID movieUuid,

        @Schema(
                description = "Title of the watched movie.",
                example = "Interstellar"
        )
        String movieTitle,

        @Schema(
                description = "Relative path of the movie poster image.",
                example = "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg"
        )
        String posterPath,

        @Schema(
                description = "Date on which the movie was watched.",
                example = "2026-07-18",
                type = "string",
                format = "date"
        )
        LocalDate watchedAt,

        @Schema(
                description = "Timestamp when the watch log was created.",
                example = "2024-01-15T10:30:00Z",
                type = "string",
                format = "date-time"
        )
        Instant createdAt
) {
}
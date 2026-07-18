package io.github.aspasiax.reverie.dto.genre;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents the data returned for a movie genre.
 *
 * @param uuid        the unique genre identifier
 * @param name        the genre name
 * @param description the genre description
 * @param icon        the frontend icon name
 * @param color       the badge color in hexadecimal format
 * @param createdAt   the creation timestamp
 * @param updatedAt   the last update timestamp
 */
@Schema(
        name = "GenreResponse",
        description = "Complete genre information returned by the Reverie API."
)
public record GenreResponse(

        @Schema(
                description = "Public UUID of the genre.",
                example = "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
        )
        UUID uuid,

        @Schema(
                description = "Genre name.",
                example = "Science Fiction"
        )
        String name,

        @Schema(
                description = "Genre description.",
                example = "Stories focused on futuristic science, technology, space exploration, or alternate realities."
        )
        String description,

        @Schema(
                description = "Frontend icon name used to represent the genre.",
                example = "rocket"
        )
        String icon,

        @Schema(
                description = "Hexadecimal color used for the genre badge.",
                example = "#6C63FF"
        )
        String color,

        @Schema(
                description = "Timestamp when the genre was created.",
                example = "2024-01-15T10:30:00Z",
                type = "string",
                format = "date-time"
        )
        Instant createdAt,

        @Schema(
                description = "Timestamp of the last genre update.",
                example = "2024-03-02T16:45:18Z",
                type = "string",
                format = "date-time"
        )
        Instant updatedAt
) {
}
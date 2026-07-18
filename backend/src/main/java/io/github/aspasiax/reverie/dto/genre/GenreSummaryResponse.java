package io.github.aspasiax.reverie.dto.genre;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Represents a compact view of a movie genre.
 *
 * <p>This response is intended for embedding inside larger
 * API responses, such as movie details.</p>
 *
 * @param uuid  the public genre identifier
 * @param name  the genre name
 * @param icon  the frontend icon name
 * @param color the frontend badge color
 */
@Schema(
        name = "GenreSummaryResponse",
        description = "Compact genre information embedded in larger API responses."
)
public record GenreSummaryResponse(

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
                description = "Frontend icon name used to represent the genre.",
                example = "rocket"
        )
        String icon,

        @Schema(
                description = "Hexadecimal color used for the genre badge.",
                example = "#6C63FF"
        )
        String color
) {
}
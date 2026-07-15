package io.github.aspasiax.reverie.dto.genre;

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
public record GenreResponse(

        UUID uuid,

        String name,

        String description,

        String icon,

        String color,

        Instant createdAt,

        Instant updatedAt
) {
}
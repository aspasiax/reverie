package io.github.aspasiax.reverie.dto.genre;

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
public record GenreSummaryResponse(

        UUID uuid,

        String name,

        String icon,

        String color
) {
}
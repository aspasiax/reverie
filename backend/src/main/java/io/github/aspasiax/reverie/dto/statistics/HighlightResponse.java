package io.github.aspasiax.reverie.dto.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Something that stands out, with the number that makes it stand out.
 *
 * @param name  what it is
 * @param count how many times it happened
 */
@Schema(
        name = "HighlightResponse",
        description = "A leading entry and the count behind it."
)
public record HighlightResponse(

        @Schema(description = "What stands out.", example = "Interstellar")
        String name,

        @Schema(description = "The count that makes it stand out.", example = "3")
        long count
) {
}
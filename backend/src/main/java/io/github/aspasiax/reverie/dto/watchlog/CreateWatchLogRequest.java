package io.github.aspasiax.reverie.dto.watchlog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents the data required to add a movie to the authenticated
 * user's watch history.
 *
 * @param movieUuid the public UUID of the watched movie
 * @param watchedAt the date on which the movie was watched
 */
@Schema(
        name = "CreateWatchLogRequest",
        description = "Data required to record a movie in the authenticated user's watch history."
)
public record CreateWatchLogRequest(

        @Schema(
                description = "Public UUID of the movie that was watched.",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Movie UUID is required.")
        UUID movieUuid,

        @Schema(
                description = "Optional date on which the movie was watched.",
                example = "2024-01-15",
                type = "string",
                format = "date"
        )
        @PastOrPresent(message = "Watch date cannot be in the future.")
        LocalDate watchedAt
) {
}
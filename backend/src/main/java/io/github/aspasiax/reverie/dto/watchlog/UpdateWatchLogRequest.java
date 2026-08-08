package io.github.aspasiax.reverie.dto.watchlog;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * Represents the fields of a watch log that may be corrected.
 *
 * <p>Only the date is editable. Moving a viewing to a different film would
 * be indistinguishable from deleting one entry and creating another, so it
 * is not offered here.</p>
 *
 * @param watchedAt the corrected viewing date, or {@code null} to clear it
 */
@Schema(
        name = "UpdateWatchLogRequest",
        description = "The viewing date to store for an existing watch log."
)
public record UpdateWatchLogRequest(

        @Schema(
                description = "Date on which the movie was watched. Omit to record no date.",
                example = "2026-01-15",
                type = "string",
                format = "date"
        )
        @PastOrPresent(message = "Watch date cannot be in the future.")
        LocalDate watchedAt
) {
}
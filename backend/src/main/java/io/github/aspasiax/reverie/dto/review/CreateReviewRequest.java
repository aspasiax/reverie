package io.github.aspasiax.reverie.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Data required to write a review for a film.
 *
 * <p>A review carries a rating, written text, or both. The author is never
 * part of the request: a review always belongs to the account making the
 * call, which is taken from the access token.</p>
 *
 * @param movieUuid  the UUID of the reviewed film
 * @param rating     the optional rating, from 1 to 10
 * @param reviewText the optional written review
 */
@Schema(
        name = "CreateReviewRequest",
        description = "Data required to write a review for a film."
)
public record CreateReviewRequest(

        @Schema(
                description = "Public UUID of the film being reviewed.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Movie UUID is required")
        UUID movieUuid,

        @Schema(
                description = "Score out of ten. Optional when written text is given.",
                example = "8",
                minimum = "1",
                maximum = "10"
        )
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 10, message = "Rating must not exceed 10")
        Integer rating,

        @Schema(
                description = "Written review. Optional when a rating is given.",
                example = "Slow in the best possible way.",
                maxLength = 5000
        )
        @Size(max = 5000, message = "Review text must not exceed 5000 characters")
        String reviewText
) {
}
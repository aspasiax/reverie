package io.github.aspasiax.reverie.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Data required to change an existing review.
 *
 * <p>The film is not part of the request. A review belongs to one film for
 * its whole life; moving it to another would be a different review.</p>
 *
 * @param rating     the optional rating, from 1 to 10
 * @param reviewText the optional written review
 */
@Schema(
        name = "UpdateReviewRequest",
        description = "Data required to change an existing review."
)
public record UpdateReviewRequest(

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
                example = "Better on a second viewing.",
                maxLength = 5000
        )
        @Size(max = 5000, message = "Review text must not exceed 5000 characters")
        String reviewText
) {
}
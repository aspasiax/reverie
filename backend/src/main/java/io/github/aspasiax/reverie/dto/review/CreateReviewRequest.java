package io.github.aspasiax.reverie.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request DTO used to create a review for a movie.
 *
 * @param movieUuid  the UUID of the reviewed movie
 * @param rating     the optional rating, from 1 to 10
 * @param reviewText the optional written review
 */
public record CreateReviewRequest(

        @NotNull(message = "Movie UUID is required")
        UUID movieUuid,

        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 10, message = "Rating must not exceed 10")
        Integer rating,

        @Size(max = 5000, message = "Review text must not exceed 5000 characters")
        String reviewText
) {
}
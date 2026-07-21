package io.github.aspasiax.reverie.dto.review;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO representing a review.
 *
 * @param uuid        the public UUID of the review
 * @param userUuid    the UUID of the user who created the review
 * @param username    the username of the review author
 * @param movieUuid   the UUID of the reviewed movie
 * @param movieTitle  the title of the reviewed movie
 * @param posterPath  the poster path of the reviewed movie
 * @param rating      the optional rating, from 1 to 10
 * @param reviewText  the optional written review
 * @param createdAt   the date and time when the review was created
 * @param updatedAt   the date and time when the review was last updated
 */
public record ReviewResponse(

        UUID uuid,

        UUID userUuid,

        String username,

        UUID movieUuid,

        String movieTitle,

        String posterPath,

        Integer rating,

        String reviewText,

        Instant createdAt,

        Instant updatedAt
) {
}
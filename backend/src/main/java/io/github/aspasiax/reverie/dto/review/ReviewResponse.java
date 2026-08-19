package io.github.aspasiax.reverie.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * A review as the API returns it.
 *
 * <p>The author is named because reviews are public: each one appears on
 * the page of the film it concerns. Enough of the author is carried to
 * show them as they appear everywhere else, so that a list of reviews
 * from several people does not have to fetch each of them separately.
 * The film is described by the few fields a list needs to show it.</p>
 *
 * @param uuid            the public UUID of the review
 * @param userUuid        the UUID of the author
 * @param username        the handle of the author
 * @param displayName     the name the author goes by
 * @param profileImageUrl the optional profile image of the author
 * @param movieUuid       the UUID of the reviewed film
 * @param movieTitle      the title of the reviewed film
 * @param posterPath      the poster path of the reviewed film
 * @param rating          the optional rating, from 1 to 10
 * @param reviewText      the optional written review
 * @param createdAt       when the review was written
 * @param updatedAt       when the review was last changed
 */
@Schema(
        name = "ReviewResponse",
        description = "A review of a film, with its author and the film it concerns."
)
public record ReviewResponse(

        @Schema(description = "Public UUID of the review.")
        UUID uuid,

        @Schema(description = "Public UUID of the author.")
        UUID userUuid,

        @Schema(description = "Unique public handle of the author.", example = "emma")
        String username,

        @Schema(description = "Name the author is displayed under.", example = "Emma")
        String displayName,

        @Schema(description = "Optional URL of the author's profile image.")
        String profileImageUrl,

        @Schema(description = "Public UUID of the reviewed film.")
        UUID movieUuid,

        @Schema(description = "Title of the reviewed film.", example = "Arrival")
        String movieTitle,

        @Schema(description = "Relative TMDB poster path.", example = "/x2FJsf1ElAgr63Y3PNPtJrcmpoe.jpg")
        String posterPath,

        @Schema(description = "Score out of ten, absent when the author gave none.", example = "8")
        Integer rating,

        @Schema(description = "Written review, absent when the author wrote none.")
        String reviewText,

        @Schema(
                description = "When the review was written.",
                type = "string",
                format = "date-time"
        )
        Instant createdAt,

        @Schema(
                description = "When the review was last changed.",
                type = "string",
                format = "date-time"
        )
        Instant updatedAt
) {
}
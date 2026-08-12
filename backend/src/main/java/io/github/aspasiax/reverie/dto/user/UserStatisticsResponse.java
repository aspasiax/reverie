package io.github.aspasiax.reverie.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * What a user has done, in numbers.
 *
 * <p>Only public activity is counted. Viewings and reviews already appear
 * on the pages of the films they concern, so summarising them reveals
 * nothing new. The watchlist is deliberately absent: what someone plans
 * to watch is theirs alone.</p>
 *
 * @param filmsWatched     distinct films the user has logged
 * @param viewingsRecorded total viewings, counting rewatches separately
 * @param reviewsWritten   reviews the user has written
 * @param averageRating    average score the user gives, null when they rate nothing
 * @param favouriteGenre   the genre they watch most, null when they watch nothing
 */
@Schema(
        name = "UserStatisticsResponse",
        description = "A summary of a user's public activity."
)
public record UserStatisticsResponse(

        @Schema(description = "Distinct films the user has logged.", example = "42")
        long filmsWatched,

        @Schema(description = "Total viewings, counting rewatches separately.", example = "51")
        long viewingsRecorded,

        @Schema(description = "Reviews the user has written.", example = "17")
        long reviewsWritten,

        @Schema(description = "Average score the user gives, absent when they rate nothing.", example = "7.8")
        Double averageRating,

        @Schema(description = "The genre the user watches most, absent when they watch nothing.", example = "Science Fiction")
        String favouriteGenre
) {
}
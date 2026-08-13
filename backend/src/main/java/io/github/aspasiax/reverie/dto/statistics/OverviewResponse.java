package io.github.aspasiax.reverie.dto.statistics;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The state of the whole application, in numbers.
 *
 * <p>Unlike the statistics of a single user, this describes the
 * installation rather than a person, which is why reading it needs a
 * capability. It counts what exists, not who did what.</p>
 *
 * @param users              accounts that exist
 * @param disabledUsers      accounts that cannot sign in
 * @param publishedFilms     films visible in the catalogue
 * @param unpublishedFilms   films prepared but not published
 * @param deletedFilms       films withdrawn and restorable
 * @param genres             genres in use
 * @param reviews            reviews written
 * @param viewings           viewings recorded
 * @param watchlistEntries   films people intend to watch
 * @param mostWatchedFilm    the most recorded film, absent when none exist
 * @param mostActiveUser     the busiest account, absent when none exist
 */
@Schema(
        name = "OverviewResponse",
        description = "A summary of the whole application."
)
public record OverviewResponse(

        @Schema(description = "Accounts that exist.", example = "4")
        long users,

        @Schema(description = "Accounts that cannot sign in.", example = "0")
        long disabledUsers,

        @Schema(description = "Films visible in the catalogue.", example = "24")
        long publishedFilms,

        @Schema(description = "Films prepared but not published.", example = "0")
        long unpublishedFilms,

        @Schema(description = "Films withdrawn and restorable.", example = "0")
        long deletedFilms,

        @Schema(description = "Genres in use.", example = "12")
        long genres,

        @Schema(description = "Reviews written.", example = "15")
        long reviews,

        @Schema(description = "Viewings recorded.", example = "25")
        long viewings,

        @Schema(description = "Films people intend to watch.", example = "10")
        long watchlistEntries,

        @Schema(description = "The most recorded film, absent when the catalogue is empty.")
        HighlightResponse mostWatchedFilm,

        @Schema(description = "The busiest account, absent when nobody has watched anything.")
        HighlightResponse mostActiveUser
) {
}
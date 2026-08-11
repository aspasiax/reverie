package io.github.aspasiax.reverie.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The orders in which the catalogue can be browsed.
 *
 * <p>Only the first is a property of a movie. The other two are computed
 * from the activity of every user, which is why they are requested by name
 * instead of through the usual sort parameter: there is no column to sort
 * by.</p>
 */
@Schema(
        name = "MovieSort",
        description = "The order in which the catalogue is returned."
)
public enum MovieSort {

    /** Alphabetically by title. */
    TITLE,

    /** Most recorded viewings first, ties broken by title. */
    MOST_WATCHED,

    /** Highest average rating first, ties broken by title. */
    TOP_RATED
}
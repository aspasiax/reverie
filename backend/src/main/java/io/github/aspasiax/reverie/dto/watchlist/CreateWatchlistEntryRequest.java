package io.github.aspasiax.reverie.dto.watchlist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Data required to add a film to the authenticated user's watchlist.
 *
 * <p>The owner is never part of the request: an entry always belongs to
 * the account making the call, which is taken from the access token.</p>
 *
 * @param movieUuid the film to add
 */
@Schema(
        name = "CreateWatchlistEntryRequest",
        description = "Data required to add a film to the watchlist."
)
public record CreateWatchlistEntryRequest(

        @Schema(
                description = "Public UUID of the film to add.",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "Movie UUID is required.")
        UUID movieUuid
) {
}
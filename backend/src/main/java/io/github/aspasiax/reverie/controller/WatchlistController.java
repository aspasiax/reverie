package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.watchlist.CreateWatchlistEntryRequest;
import io.github.aspasiax.reverie.dto.watchlist.WatchlistEntryResponse;
import io.github.aspasiax.reverie.service.IWatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Exposes REST endpoints for the authenticated user's watchlist.
 *
 * <p>
 * A watchlist is private. Every endpoint here applies to the account
 * making the request, so none of them accepts a user identifier.
 * </p>
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Watchlist",
        description = "Operations for managing the films a user intends to watch."
)
@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final IWatchlistService watchlistService;

    /**
     * Returns a page of the authenticated user's watchlist.
     *
     * @param pageable the requested page and sort order
     * @return a page of the films the user intends to watch
     */
    @Operation(
            summary = "Get my watchlist",
            description = "Returns a page of the films the authenticated user intends to watch, most recently added first."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Watchlist retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('WATCHLIST_READ')")
    public ResponseEntity<PageResponse<WatchlistEntryResponse>> findMyWatchlist(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(watchlistService.findMyWatchlist(pageable));
    }

    /**
     * Adds a film to the authenticated user's watchlist.
     *
     * @param request the film to add
     * @return the created entry
     */
    @Operation(
            summary = "Add a film to my watchlist",
            description = "Adds a film to the authenticated user's watchlist. A film can only be on the list once."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Film added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Film not found"),
            @ApiResponse(responseCode = "409", description = "The film is already on the watchlist")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('WATCHLIST_CREATE')")
    public ResponseEntity<WatchlistEntryResponse> add(
            @Valid @RequestBody CreateWatchlistEntryRequest request
    ) {
        WatchlistEntryResponse createdEntry = watchlistService.add(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdEntry);
    }

    /**
     * Removes an entry from the authenticated user's watchlist.
     *
     * @param uuid the entry UUID
     * @return an empty {@code 204 No Content} response
     */
    @Operation(
            summary = "Remove a film from my watchlist",
            description = "Removes an entry from the authenticated user's watchlist."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entry removed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "The entry belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Entry not found")
    })
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('WATCHLIST_DELETE')")
    public ResponseEntity<Void> remove(
            @PathVariable UUID uuid
    ) {
        watchlistService.remove(uuid);

        return ResponseEntity.noContent().build();
    }
}
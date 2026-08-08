package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.watchlog.CreateWatchLogRequest;
import io.github.aspasiax.reverie.dto.watchlog.UpdateWatchLogRequest;
import io.github.aspasiax.reverie.dto.watchlog.WatchLogResponse;
import io.github.aspasiax.reverie.service.IWatchLogService;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Exposes REST endpoints for managing the authenticated user's watch history.
 *
 * <p>
 * Watch-log operations require authentication and the corresponding
 * watch-log capabilities. Users may retrieve, create and delete entries
 * from their own watch history.
 * </p>
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Watch Logs",
        description = "Operations for viewing and managing the authenticated user's watch history."
)
@RestController
@RequestMapping("/api/watch-logs")
@RequiredArgsConstructor
public class WatchLogController {

    private final IWatchLogService watchLogService;

    /**
     * Returns the authenticated user's active watch logs.
     *
     * @return the authenticated user's watch history
     */
    @Operation(
            summary = "Get my watch history",
            description = "Returns all active watch logs belonging to the authenticated user, ordered from newest to oldest."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Watch history retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions"
            )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('WATCH_LOG_READ')")
    public ResponseEntity<PageResponse<WatchLogResponse>> findMyWatchLogs(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(watchLogService.findMyWatchLogs(pageable));
    }

    /**
     * Creates a new watch log for the authenticated user.
     *
     * @param request the validated watch-log creation request
     * @return the created watch log
     */
    @Operation(
            summary = "Create a watch log",
            description = "Adds a movie to the authenticated user's watch history."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Watch log created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid watch-log data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movie not found"
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('WATCH_LOG_CREATE')")
    public ResponseEntity<WatchLogResponse> create(
            @Valid @RequestBody CreateWatchLogRequest request
    ) {
        WatchLogResponse createdWatchLog =
                watchLogService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdWatchLog);
    }

    /**
     * Corrects the viewing date of one of the authenticated user's watch logs.
     *
     * @param uuid    the watch-log UUID
     * @param request the validated update request
     * @return the updated watch log
     */
    @Operation(
            summary = "Update a watch log",
            description = "Corrects the viewing date of a watch log that belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Watch log updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid watch-log data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Watch log belongs to another user or permissions are insufficient"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Watch log not found"
            )
    })
    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('WATCH_LOG_UPDATE')")
    public ResponseEntity<WatchLogResponse> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateWatchLogRequest request
    ) {
        return ResponseEntity.ok(
                watchLogService.update(uuid, request)
        );
    }

    /**
     * Soft deletes one of the authenticated user's watch logs.
     *
     * @param uuid the watch-log UUID
     * @return an empty {@code 204 No Content} response
     */
    @Operation(
            summary = "Delete a watch log",
            description = "Soft-deletes a watch log identified by its public UUID when it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Watch log deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Watch log belongs to another user or permissions are insufficient"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Watch log not found"
            )
    })
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('WATCH_LOG_DELETE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID uuid
    ) {
        watchLogService.delete(uuid);

        return ResponseEntity.noContent().build();
    }
}
package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.movie.CreateMovieRequest;
import io.github.aspasiax.reverie.dto.movie.MovieResponse;
import io.github.aspasiax.reverie.dto.movie.UpdateMovieRequest;
import io.github.aspasiax.reverie.service.IMovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Exposes REST endpoints for movie retrieval and administration.
 *
 * <p>
 * Read operations are available to users with the
 * {@code MOVIE_READ} capability, while create, update and delete
 * operations require their corresponding movie capabilities.
 * </p>
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Movies",
        description = "Operations for browsing and managing the movie catalog."
)
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final IMovieService movieService;

    /**
     * Returns a page of published movies.
     *
     * @param pageable the requested page and sort order
     * @return a page of published movies
     */
    @Operation(
            summary = "Get all published movies",
            description = "Returns a page of published movies. Use the page, size and sort parameters to navigate the catalogue."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('MOVIE_READ')")
    public ResponseEntity<PageResponse<MovieResponse>> findAllPublished(
            @PageableDefault(size = 20, sort = "title") Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.findAllPublished(pageable));
    }

    /**
     * Returns a page of every movie that has not been deleted.
     *
     * @param pageable the requested page and sort order
     * @return a page of active movies, published or not
     */
    @Operation(
            summary = "Get all movies for administration",
            description = "Returns a page of every movie that has not been deleted, including the ones that are not published yet."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('MOVIE_UPDATE')")
    public ResponseEntity<PageResponse<MovieResponse>> findAllActive(
            @PageableDefault(size = 20, sort = "title") Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.findAllActive(pageable));
    }

    /**
     * Returns a page of soft-deleted movies.
     *
     * @param pageable the requested page and sort order
     * @return a page of soft-deleted movies
     */
    @Operation(
            summary = "Get all deleted movies",
            description = "Returns a page of soft-deleted movies, which are the ones that can be restored."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted movies retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/deleted")
    @PreAuthorize("hasAuthority('MOVIE_UPDATE')")
    public ResponseEntity<PageResponse<MovieResponse>> findAllDeleted(
            @PageableDefault(size = 20, sort = "title") Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.findAllDeleted(pageable));
    }

    /**
     * Returns an active movie by its public UUID.
     *
     * @param uuid the movie UUID
     * @return the matching movie
     */
    @Operation(
            summary = "Get a movie by UUID",
            description = "Returns the details of a single active movie identified by its public UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movie retrieved successfully"
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
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('MOVIE_READ')")
    public ResponseEntity<MovieResponse> findByUuid(
            @PathVariable UUID uuid
    ) {
        return ResponseEntity.ok(movieService.findByUuid(uuid));
    }

    /**
     * Creates a new movie.
     *
     * @param request the validated movie creation request
     * @return the created movie
     */
    @Operation(
            summary = "Create a movie",
            description = "Creates a new movie and adds it to the catalog."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Movie created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid movie data"
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
                    responseCode = "409",
                    description = "Movie already exists"
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('MOVIE_CREATE')")
    public ResponseEntity<MovieResponse> create(
            @Valid @RequestBody CreateMovieRequest request
    ) {
        MovieResponse createdMovie = movieService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMovie);
    }

    /**
     * Updates an existing movie.
     *
     * @param uuid    the movie UUID
     * @param request the validated movie update request
     * @return the updated movie
     */
    @Operation(
            summary = "Update a movie",
            description = "Updates an existing movie identified by its public UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movie updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid movie data"
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Movie update conflicts with existing data"
            )
    })
    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('MOVIE_UPDATE')")
    public ResponseEntity<MovieResponse> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateMovieRequest request
    ) {
        return ResponseEntity.ok(
                movieService.update(uuid, request)
        );
    }

    /**
     * Soft deletes an existing movie.
     *
     * @param uuid the movie UUID
     * @return an empty {@code 204 No Content} response
     */
    @Operation(
            summary = "Delete a movie",
            description = "Soft-deletes an existing movie identified by its public UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Movie deleted successfully"
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
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('MOVIE_DELETE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID uuid
    ) {
        movieService.delete(uuid);

        return ResponseEntity.noContent().build();
    }

    /**
     * Restores a previously soft-deleted movie.
     *
     * @param uuid the movie UUID
     * @return the restored movie
     */
    @Operation(
            summary = "Restore a movie",
            description = "Restores a movie that was previously soft-deleted, making it visible again."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie restored successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "409", description = "Movie is not deleted")
    })
    @PostMapping("/{uuid}/restore")
    @PreAuthorize("hasAuthority('MOVIE_UPDATE')")
    public ResponseEntity<MovieResponse> restore(
            @PathVariable UUID uuid
    ) {
        return ResponseEntity.ok(movieService.restore(uuid));
    }

    /**
     * Publishes a movie, making it visible in the catalogue.
     *
     * @param uuid the movie UUID
     * @return the published movie
     */
    @Operation(
            summary = "Publish a movie",
            description = "Makes a movie visible in the public catalogue."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie published successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PostMapping("/{uuid}/publish")
    @PreAuthorize("hasAuthority('MOVIE_UPDATE')")
    public ResponseEntity<MovieResponse> publish(
            @PathVariable UUID uuid
    ) {
        return ResponseEntity.ok(movieService.publish(uuid));
    }

    /**
     * Withdraws a movie from the catalogue without deleting it.
     *
     * @param uuid the movie UUID
     * @return the unpublished movie
     */
    @Operation(
            summary = "Unpublish a movie",
            description = "Withdraws a movie from the public catalogue without deleting it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movie unpublished successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PostMapping("/{uuid}/unpublish")
    @PreAuthorize("hasAuthority('MOVIE_UPDATE')")
    public ResponseEntity<MovieResponse> unpublish(
            @PathVariable UUID uuid
    ) {
        return ResponseEntity.ok(movieService.unpublish(uuid));
    }
}
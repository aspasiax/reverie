package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.genre.CreateGenreRequest;
import io.github.aspasiax.reverie.dto.genre.GenreResponse;
import io.github.aspasiax.reverie.dto.genre.UpdateGenreRequest;
import io.github.aspasiax.reverie.service.IGenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.UUID;

/**
 * Exposes REST endpoints for genre retrieval and administration.
 *
 * <p>
 * Read operations require the {@code GENRE_READ} capability,
 * while create, update and delete operations require their
 * corresponding genre capabilities.
 * </p>
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Genres",
        description = "Operations for browsing and managing movie genres."
)
@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final IGenreService genreService;

    /**
     * Returns all active genres in alphabetical order.
     *
     * @return a list containing all active genres
     */
    @Operation(
            summary = "Get all genres",
            description = "Returns all active movie genres in alphabetical order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genres retrieved successfully"
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
    @PreAuthorize("hasAuthority('GENRE_READ')")
    public ResponseEntity<List<GenreResponse>> findAll() {
        return ResponseEntity.ok(genreService.findAll());
    }

    /**
     * Returns an active genre by its public UUID.
     *
     * @param uuid the genre UUID
     * @return the matching genre
     */
    @Operation(
            summary = "Get a genre by UUID",
            description = "Returns the details of a single active genre identified by its public UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genre retrieved successfully"
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
                    description = "Genre not found"
            )
    })
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('GENRE_READ')")
    public ResponseEntity<GenreResponse> findByUuid(
            @PathVariable UUID uuid
    ) {
        return ResponseEntity.ok(
                genreService.findByUuid(uuid)
        );
    }

    /**
     * Creates a new genre.
     *
     * @param request the validated genre creation request
     * @return the created genre
     */
    @Operation(
            summary = "Create a genre",
            description = "Creates a new movie genre."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Genre created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid genre data"
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
                    description = "Genre already exists"
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('GENRE_CREATE')")
    public ResponseEntity<GenreResponse> create(
            @Valid @RequestBody CreateGenreRequest request
    ) {
        GenreResponse createdGenre =
                genreService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdGenre);
    }

    /**
     * Updates an existing genre.
     *
     * @param uuid    the genre UUID
     * @param request the validated genre update request
     * @return the updated genre
     */
    @Operation(
            summary = "Update a genre",
            description = "Updates an existing genre identified by its public UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Genre updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid genre data"
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
                    description = "Genre not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Genre update conflicts with existing data"
            )
    })
    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('GENRE_UPDATE')")
    public ResponseEntity<GenreResponse> update(
            @PathVariable UUID uuid,
            @Valid @RequestBody UpdateGenreRequest request
    ) {
        return ResponseEntity.ok(
                genreService.update(uuid, request)
        );
    }

    /**
     * Soft deletes an existing genre.
     *
     * @param uuid the genre UUID
     * @return an empty {@code 204 No Content} response
     */
    @Operation(
            summary = "Delete a genre",
            description = "Soft-deletes an existing genre identified by its public UUID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Genre deleted successfully"
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
                    description = "Genre not found"
            )
    })
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('GENRE_DELETE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID uuid
    ) {
        genreService.delete(uuid);

        return ResponseEntity.noContent().build();
    }
}
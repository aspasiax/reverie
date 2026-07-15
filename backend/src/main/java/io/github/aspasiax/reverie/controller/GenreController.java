package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.genre.CreateGenreRequest;
import io.github.aspasiax.reverie.dto.genre.GenreResponse;
import io.github.aspasiax.reverie.dto.genre.UpdateGenreRequest;
import io.github.aspasiax.reverie.service.IGenreService;
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
 * <p>Genre operations are protected using fine-grained
 * authorization capabilities.</p>
 */
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
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('GENRE_DELETE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID uuid
    ) {
        genreService.delete(uuid);

        return ResponseEntity.noContent().build();
    }
}
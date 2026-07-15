package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.movie.CreateMovieRequest;
import io.github.aspasiax.reverie.dto.movie.MovieResponse;
import io.github.aspasiax.reverie.dto.movie.UpdateMovieRequest;
import io.github.aspasiax.reverie.service.IMovieService;
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
 * Exposes REST endpoints for movie retrieval and administration.
 *
 * <p>Read operations are currently available to authenticated users,
 * while create, update and delete operations require the corresponding
 * movie capabilities.</p>
 */
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final IMovieService movieService;

    /**
     * Returns all active movies.
     *
     * @return a list containing all active movies
     */
    @GetMapping
    @PreAuthorize("hasAuthority('MOVIE_READ')")
    public ResponseEntity<List<MovieResponse>> findAll() {
        return ResponseEntity.ok(movieService.findAll());
    }

    /**
     * Returns an active movie by its public UUID.
     *
     * @param uuid the movie UUID
     * @return the matching movie
     */
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
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('MOVIE_DELETE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID uuid
    ) {
        movieService.delete(uuid);

        return ResponseEntity.noContent().build();
    }
}
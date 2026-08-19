package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.review.CreateReviewRequest;
import io.github.aspasiax.reverie.dto.review.ReviewResponse;
import io.github.aspasiax.reverie.dto.review.UpdateReviewRequest;
import io.github.aspasiax.reverie.service.IReviewService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Exposes REST endpoints for reading and writing film reviews.
 *
 * <p>
 * Reviews are public to read and private to change: any signed in user
 * may see them, while only their author may edit or remove one.
 * </p>
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(
        name = "Reviews",
        description = "Operations for reading and writing film reviews."
)
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    /**
     * Returns a page of the most recent reviews written by anyone.
     *
     * @param pageable the requested page and sort order
     * @return a page of reviews from across the catalogue
     */
    @Operation(
            summary = "Get recent reviews",
            description = "Returns a page of the reviews most recently written by anyone, newest first."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('REVIEW_READ')")
    public ResponseEntity<PageResponse<ReviewResponse>> findRecentReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.findRecentReviews(pageable));
    }

    /**
     * Returns a page of the reviews written for a film.
     *
     * @param movieUuid the movie UUID
     * @param pageable  the requested page and sort order
     * @return a page of the film's reviews
     */
    @Operation(
            summary = "Get the reviews of a film",
            description = "Returns a page of the reviews written for a film, newest first."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/movie/{movieUuid}")
    @PreAuthorize("hasAuthority('REVIEW_READ')")
    public ResponseEntity<PageResponse<ReviewResponse>> findMovieReviews(
            @PathVariable UUID movieUuid,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                reviewService.findMovieReviews(movieUuid, pageable)
        );
    }

    /**
     * Returns a page of the reviews written by a user.
     *
     * @param userUuid the user UUID
     * @param pageable the requested page and sort order
     * @return a page of the user's reviews
     */
    @Operation(
            summary = "Get the reviews of a user",
            description = "Returns a page of the reviews a user has written, newest first."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userUuid}")
    @PreAuthorize("hasAuthority('REVIEW_READ')")
    public ResponseEntity<PageResponse<ReviewResponse>> findUserReviews(
            @PathVariable UUID userUuid,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                reviewService.findUserReviews(userUuid, pageable)
        );
    }

    /**
     * Returns a page of the reviews written by the authenticated user.
     *
     * @param pageable the requested page and sort order
     * @return a page of the authenticated user's reviews
     */
    @Operation(
            summary = "Get my reviews",
            description = "Returns a page of the reviews the authenticated user has written, newest first."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('REVIEW_READ')")
    public ResponseEntity<PageResponse<ReviewResponse>> findMyReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.findMyReviews(pageable));
    }

    /**
     * Creates a review for a film the authenticated user has watched.
     *
     * @param request the validated review creation request
     * @return the created review
     */
    @Operation(
            summary = "Write a review",
            description = "Creates a review for a film. The film must already be logged as watched, and a user holds at most one review per film."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review data or missing watch log"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "409", description = "A review already exists for this movie")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('REVIEW_CREATE')")
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody CreateReviewRequest request
    ) {
        ReviewResponse createdReview = reviewService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReview);
    }

    /**
     * Updates a review owned by the authenticated user.
     *
     * @param reviewUuid the review UUID
     * @param request    the validated review update request
     * @return the updated review
     */
    @Operation(
            summary = "Update a review",
            description = "Updates a review. Only the author of a review may change it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Review belongs to another user or permissions are insufficient"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PutMapping("/{reviewUuid}")
    @PreAuthorize("hasAuthority('REVIEW_UPDATE')")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable UUID reviewUuid,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        return ResponseEntity.ok(
                reviewService.update(reviewUuid, request)
        );
    }

    /**
     * Soft deletes a review owned by the authenticated user.
     *
     * @param reviewUuid the review UUID
     * @return an empty {@code 204 No Content} response
     */
    @Operation(
            summary = "Delete a review",
            description = "Soft-deletes a review. Only the author of a review may remove it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Review belongs to another user or permissions are insufficient"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/{reviewUuid}")
    @PreAuthorize("hasAuthority('REVIEW_DELETE')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID reviewUuid
    ) {
        reviewService.delete(reviewUuid);

        return ResponseEntity.noContent().build();
    }
}
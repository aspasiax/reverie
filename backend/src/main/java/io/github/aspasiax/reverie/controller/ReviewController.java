package io.github.aspasiax.reverie.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller responsible for movie review operations.
 * <p>
 * Provides endpoints for creating, updating, deleting,
 * and retrieving reviews.
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(
        name = "Reviews",
        description = "Review management endpoints."
)
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final IReviewService reviewService;

    /**
     * Returns all reviews for a movie.
     *
     * @param movieUuid movie identifier
     * @return movie reviews
     */
    @PreAuthorize("hasAuthority('REVIEW_READ')")
    @GetMapping("/movie/{movieUuid}")
    @Operation(summary = "Get reviews for a movie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public List<ReviewResponse> getMovieReviews(
            @PathVariable UUID movieUuid
    ) {
        return reviewService.findMovieReviews(movieUuid);
    }

    /**
     * Returns all reviews created by the authenticated user.
     *
     * @return authenticated user's reviews
     */
    @PreAuthorize("hasAuthority('REVIEW_READ')")
    @GetMapping("/me")
    @Operation(summary = "Get my reviews")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    public List<ReviewResponse> getMyReviews() {
        return reviewService.findMyReviews();
    }

    /**
     * Creates a new review.
     *
     * @param request review creation request
     * @return created review
     */
    @PreAuthorize("hasAuthority('REVIEW_CREATE')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create review")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review data or missing watch log"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "409", description = "A review already exists for this movie")
    })
    public ReviewResponse createReview(
            @Valid @RequestBody CreateReviewRequest request
    ) {
        return reviewService.create(request);
    }

    /**
     * Updates an existing review.
     *
     * @param reviewUuid review identifier
     * @param request review update request
     * @return updated review
     */
    @PreAuthorize("hasAuthority('REVIEW_UPDATE')")
    @PutMapping("/{reviewUuid}")
    @Operation(summary = "Update review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid review data"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Review belongs to another user or permissions are insufficient"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public ReviewResponse updateReview(
            @PathVariable UUID reviewUuid,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        return reviewService.update(reviewUuid, request);
    }

    /**
     * Deletes a review.
     *
     * @param reviewUuid review identifier
     */
    @PreAuthorize("hasAuthority('REVIEW_DELETE')")
    @DeleteMapping("/{reviewUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete review")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Review belongs to another user or permissions are insufficient"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    public void deleteReview(
            @PathVariable UUID reviewUuid
    ) {
        reviewService.delete(reviewUuid);
    }
}
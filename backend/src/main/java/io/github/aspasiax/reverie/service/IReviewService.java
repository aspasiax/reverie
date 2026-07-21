package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.review.CreateReviewRequest;
import io.github.aspasiax.reverie.dto.review.ReviewResponse;
import io.github.aspasiax.reverie.dto.review.UpdateReviewRequest;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing movie reviews.
 */
public interface IReviewService {

    /**
     * Returns all active reviews for a movie.
     *
     * @param movieUuid the movie UUID
     * @return a list of review responses
     */
    List<ReviewResponse> findMovieReviews(UUID movieUuid);

    /**
     * Returns all active reviews created by the authenticated user.
     *
     * @return a list of review responses
     */
    List<ReviewResponse> findMyReviews();

    /**
     * Creates a new review.
     *
     * @param request the review creation request
     * @return the created review
     */
    ReviewResponse create(CreateReviewRequest request);

    /**
     * Updates an existing review.
     *
     * @param reviewUuid the review UUID
     * @param request    the update request
     * @return the updated review
     */
    ReviewResponse update(UUID reviewUuid, UpdateReviewRequest request);

    /**
     * Soft deletes a review.
     *
     * @param reviewUuid the review UUID
     */
    void delete(UUID reviewUuid);
}
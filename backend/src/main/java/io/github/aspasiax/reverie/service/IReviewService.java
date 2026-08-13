package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.review.CreateReviewRequest;
import io.github.aspasiax.reverie.dto.review.ReviewResponse;
import io.github.aspasiax.reverie.dto.review.UpdateReviewRequest;
import org.springframework.data.domain.Pageable;

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
     * @param pageable the requested page and sort order
     * @return a list of review responses
     */
    PageResponse<ReviewResponse> findMovieReviews(UUID movieUuid, Pageable pageable);

    /**
     * Retrieves the reviews written by a user.
     *
     * <p>Reviews are public: each one already appears on the page of the
     * film it concerns, carrying the name of whoever wrote it. Gathering
     * them by author exposes nothing that was hidden.</p>
     *
     * @param userUuid the public user identifier
     * @param pageable the requested page and sort order
     * @return the user's active reviews
     */
    PageResponse<ReviewResponse> findUserReviews(UUID userUuid, Pageable pageable);

    /**
     * Returns all active reviews created by the authenticated user.
     *
     * @param pageable the requested page and sort order
     * @return a list of review responses
     */
    PageResponse<ReviewResponse> findMyReviews(Pageable pageable);

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
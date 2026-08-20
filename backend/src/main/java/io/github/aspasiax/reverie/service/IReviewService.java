package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.review.CreateReviewRequest;
import io.github.aspasiax.reverie.dto.review.ReviewResponse;
import io.github.aspasiax.reverie.dto.review.UpdateReviewRequest;
import io.github.aspasiax.reverie.exception.DuplicateReviewException;
import io.github.aspasiax.reverie.exception.InvalidReviewException;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import io.github.aspasiax.reverie.exception.ReviewAccessDeniedException;
import io.github.aspasiax.reverie.exception.ReviewNotFoundException;
import io.github.aspasiax.reverie.exception.UserNotFoundException;
import io.github.aspasiax.reverie.exception.WatchLogRequiredException;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service for managing movie reviews.
 *
 * <p>Every listing here returns its reviews newest first.</p>
 */
public interface IReviewService {

    /**
     * Returns a page of the most recent reviews written by anyone.
     *
     * <p>Reviews of unpublished films are left out: a draft does not appear
     * in the catalogue, so a review of one must not appear either.</p>
     *
     * @param pageable the requested page and sort order
     * @return a page of reviews from across the catalogue
     */
    PageResponse<ReviewResponse> findRecentReviews(Pageable pageable);

    /**
     * Returns the active reviews written for a film.
     *
     * @param movieUuid the public movie identifier
     * @param pageable  the requested page and sort order
     * @return a page of the film's reviews
     * @throws MovieNotFoundException if the film does not exist or has been
     *                                soft deleted
     */
    PageResponse<ReviewResponse> findMovieReviews(UUID movieUuid, Pageable pageable);

    /**
     * Returns the active reviews written by a user.
     *
     * <p>Reviews are public: each one already appears on the page of the
     * film it concerns, carrying the name of whoever wrote it. Gathering
     * them by author exposes nothing that was hidden.</p>
     *
     * @param userUuid the public user identifier
     * @param pageable the requested page and sort order
     * @return a page of the user's reviews
     * @throws UserNotFoundException if no such user exists
     */
    PageResponse<ReviewResponse> findUserReviews(UUID userUuid, Pageable pageable);

    /**
     * Returns the active reviews written by the authenticated user.
     *
     * @param pageable the requested page and sort order
     * @return a page of the authenticated user's reviews
     */
    PageResponse<ReviewResponse> findMyReviews(Pageable pageable);

    /**
     * Creates a review for a film.
     *
     * <p>The authenticated user must already have an active watch log for
     * the film, and must not already hold an active review of it.</p>
     *
     * @param request the review creation request
     * @return the newly created review
     * @throws MovieNotFoundException    if the film does not exist or has
     *                                   been soft deleted
     * @throws InvalidReviewException    if both the rating and the review
     *                                   text are empty
     * @throws WatchLogRequiredException if the user has not logged the film
     *                                   as watched
     * @throws DuplicateReviewException  if the user already holds an active
     *                                   review of the film
     */
    ReviewResponse create(CreateReviewRequest request);

    /**
     * Replaces the contents of an existing review.
     *
     * <p>Both fields are replaced by the values carried in the request, so
     * omitting one clears it. What remains must still hold either a rating
     * or some text.</p>
     *
     * @param reviewUuid the public review identifier
     * @param request    the review update request
     * @return the updated review
     * @throws ReviewNotFoundException     if the review does not exist or
     *                                     has been soft deleted
     * @throws ReviewAccessDeniedException if the review belongs to another
     *                                     user
     * @throws InvalidReviewException      if both resulting fields are empty
     */
    ReviewResponse update(UUID reviewUuid, UpdateReviewRequest request);

    /**
     * Withdraws a review.
     *
     * <p>Nothing is removed from the database. The review is marked deleted
     * through the soft deletion the entities share, which is what allows it
     * to stop counting towards a film's rating without the film forgetting
     * it was ever rated.</p>
     *
     * @param reviewUuid the public review identifier
     * @throws ReviewNotFoundException     if the review does not exist or
     *                                     has been soft deleted
     * @throws ReviewAccessDeniedException if the review belongs to another
     *                                     user
     */
    void delete(UUID reviewUuid);
}
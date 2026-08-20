package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.Review;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.review.CreateReviewRequest;
import io.github.aspasiax.reverie.dto.review.ReviewResponse;
import io.github.aspasiax.reverie.dto.review.UpdateReviewRequest;
import io.github.aspasiax.reverie.exception.*;
import io.github.aspasiax.reverie.mapper.ReviewMapper;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.ReviewRepository;
import io.github.aspasiax.reverie.repository.UserRepository;
import io.github.aspasiax.reverie.repository.WatchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Provides the business logic for managing movie reviews.
 *
 * <p>The service enforces the following application rules:</p>
 *
 * <ul>
 *     <li>A user must have an active watch log for the reviewed movie.</li>
 *     <li>A user may have only one active review per movie.</li>
 *     <li>A review must contain either a rating or non-blank text.</li>
 *     <li>Only the owner of a review may update or delete it.</li>
 *     <li>Reviews are removed using soft deletion.</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final WatchLogRepository watchLogRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final ICurrentUserService currentUserService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> findRecentReviews(Pageable pageable) {
        Page<ReviewResponse> page = reviewRepository
                .findAllPublished(pageable)
                .map(reviewMapper::toResponse);

        return PageResponse.from(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<ReviewResponse> findMovieReviews(
            UUID movieUuid,
            Pageable pageable
    ) {
        findActiveMovie(movieUuid);

        Page<ReviewResponse> page = reviewRepository
                .findAllByMovieUuidAndDeletedFalse(movieUuid, pageable)
                .map(reviewMapper::toResponse);

        return PageResponse.from(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<ReviewResponse> findUserReviews(
            UUID userUuid,
            Pageable pageable
    ) {
        /*
         * A listing of somebody's reviews only makes sense if the somebody
         * exists. Without this the endpoint would answer an empty page for
         * any identifier at all, which reads as "wrote nothing" rather than
         * "is nobody".
         */
        if (!userRepository.existsByUuid(userUuid)) {
            throw new UserNotFoundException(userUuid);
        }

        Page<ReviewResponse> page = reviewRepository
                .findAllByUserUuidAndDeletedFalse(userUuid, pageable)
                .map(reviewMapper::toResponse);

        return PageResponse.from(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<ReviewResponse> findMyReviews(Pageable pageable) {
        User currentUser = currentUserService.getCurrentUser();

        Page<ReviewResponse> page = reviewRepository
                .findAllByUserUuidAndDeletedFalse(currentUser.getUuid(), pageable)
                .map(reviewMapper::toResponse);

        return PageResponse.from(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ReviewResponse create(CreateReviewRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Movie movie = findActiveMovie(request.movieUuid());

        String normalizedReviewText = normalizeReviewText(
                request.reviewText()
        );

        validateReviewContent(
                request.rating(),
                normalizedReviewText
        );

        validateWatchLogExists(
                currentUser.getUuid(),
                movie.getUuid()
        );

        validateReviewDoesNotExist(
                currentUser.getUuid(),
                movie.getUuid()
        );

        Review review = new Review();

        review.setUser(currentUser);
        review.setMovie(movie);
        review.setRating(request.rating());
        review.setReviewText(normalizedReviewText);

        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toResponse(savedReview);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ReviewResponse update(
            UUID reviewUuid,
            UpdateReviewRequest request
    ) {
        User currentUser = currentUserService.getCurrentUser();
        Review review = findActiveReview(reviewUuid);

        validateOwnership(review, currentUser.getUuid());

        String normalizedReviewText = normalizeReviewText(
                request.reviewText()
        );

        validateReviewContent(
                request.rating(),
                normalizedReviewText
        );

        review.setRating(request.rating());
        review.setReviewText(normalizedReviewText);

        Review updatedReview = reviewRepository.save(review);

        return reviewMapper.toResponse(updatedReview);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(UUID reviewUuid) {
        User currentUser = currentUserService.getCurrentUser();
        Review review = findActiveReview(reviewUuid);

        validateOwnership(review, currentUser.getUuid());

        review.softDelete();
        reviewRepository.save(review);
    }

    /**
     * Retrieves an active movie by its UUID.
     *
     * @param movieUuid the movie UUID
     * @return the active movie
     * @throws MovieNotFoundException if no active movie exists with the
     *                                supplied UUID
     */
    private Movie findActiveMovie(UUID movieUuid) {
        return movieRepository
                .findByUuidAndDeletedFalse(movieUuid)
                .orElseThrow(() -> new MovieNotFoundException(movieUuid));
    }

    /**
     * Retrieves an active review by its UUID.
     *
     * @param reviewUuid the review UUID
     * @return the active review
     * @throws ReviewNotFoundException if no active review exists with the
     *                                 supplied UUID
     */
    private Review findActiveReview(UUID reviewUuid) {
        return reviewRepository
                .findByUuidAndDeletedFalse(reviewUuid)
                .orElseThrow(() -> new ReviewNotFoundException(reviewUuid));
    }

    /**
     * Verifies that the authenticated user owns the supplied review.
     *
     * @param review  the review involved in the operation
     * @param userUuid the UUID of the authenticated user
     * @throws ReviewAccessDeniedException if the review belongs to a
     *                                     different user
     */
    private void validateOwnership(
            Review review,
            UUID userUuid
    ) {
        if (!review.getUser().getUuid().equals(userUuid)) {
            throw new ReviewAccessDeniedException(review.getUuid());
        }
    }

    /**
     * Verifies that the user has an active watch log for the movie.
     *
     * @param userUuid  the authenticated user's UUID
     * @param movieUuid the movie UUID
     * @throws WatchLogRequiredException if an active watch log does not
     *                                   exist
     */
    private void validateWatchLogExists(
            UUID userUuid,
            UUID movieUuid
    ) {
        boolean watchLogExists =
                watchLogRepository
                        .existsByUserUuidAndMovieUuidAndDeletedFalse(
                                userUuid,
                                movieUuid
                        );

        if (!watchLogExists) {
            throw new WatchLogRequiredException("creating a review");
        }
    }

    /**
     * Verifies that the user does not already have an active review
     * for the movie.
     *
     * @param userUuid  the authenticated user's UUID
     * @param movieUuid the movie UUID
     * @throws DuplicateReviewException if an active review already exists
     */
    private void validateReviewDoesNotExist(
            UUID userUuid,
            UUID movieUuid
    ) {
        boolean reviewExists =
                reviewRepository
                        .existsByUserUuidAndMovieUuidAndDeletedFalse(
                                userUuid,
                                movieUuid
                        );

        if (reviewExists) {
            throw new DuplicateReviewException();
        }
    }

    /**
     * Verifies that a review contains meaningful content.
     *
     * <p>A review is considered valid when it contains either a rating
     * or non-blank review text.</p>
     *
     * @param rating     the review rating
     * @param reviewText the normalized review text
     * @throws InvalidReviewException if both values are empty
     */
    private void validateReviewContent(
            Integer rating,
            String reviewText
    ) {
        if (rating == null && reviewText == null) {
            throw new InvalidReviewException();
        }
    }

    /**
     * Normalizes review text before it is stored.
     *
     * <p>Leading and trailing whitespace is removed. Blank text is
     * converted to {@code null}, preventing whitespace-only content from
     * being treated as a meaningful review.</p>
     *
     * @param reviewText the submitted review text
     * @return trimmed review text, or {@code null} when the submitted value
     *         is null or blank
     */
    private String normalizeReviewText(String reviewText) {
        if (reviewText == null || reviewText.isBlank()) {
            return null;
        }

        return reviewText.trim();
    }
}
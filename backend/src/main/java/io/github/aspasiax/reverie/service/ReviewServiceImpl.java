package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.Review;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.review.CreateReviewRequest;
import io.github.aspasiax.reverie.dto.review.ReviewResponse;
import io.github.aspasiax.reverie.dto.review.UpdateReviewRequest;
import io.github.aspasiax.reverie.exception.DuplicateReviewException;
import io.github.aspasiax.reverie.exception.InvalidReviewException;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import io.github.aspasiax.reverie.exception.ReviewAccessDeniedException;
import io.github.aspasiax.reverie.exception.ReviewNotFoundException;
import io.github.aspasiax.reverie.exception.WatchLogRequiredException;
import io.github.aspasiax.reverie.mapper.ReviewMapper;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.ReviewRepository;
import io.github.aspasiax.reverie.repository.WatchLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
public class ReviewServiceImpl implements IReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final WatchLogRepository watchLogRepository;
    private final ReviewMapper reviewMapper;
    private final ICurrentUserService currentUserService;

    /**
     * Creates a new review service.
     *
     * @param reviewRepository   the repository used to access reviews
     * @param movieRepository    the repository used to access movies
     * @param watchLogRepository the repository used to access watch logs
     * @param reviewMapper       the mapper used to convert reviews to DTOs
     * @param currentUserService the service used to retrieve the
     *                           authenticated user
     */
    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            MovieRepository movieRepository,
            WatchLogRepository watchLogRepository,
            ReviewMapper reviewMapper,
            ICurrentUserService currentUserService
    ) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.watchLogRepository = watchLogRepository;
        this.reviewMapper = reviewMapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Returns all active reviews for a specific movie.
     *
     * <p>The reviews are returned from newest to oldest.</p>
     *
     * @param movieUuid the UUID of the movie
     * @return the active reviews associated with the movie
     * @throws MovieNotFoundException if the movie does not exist or has
     *                                been soft deleted
     */
    @Override
    public List<ReviewResponse> findMovieReviews(UUID movieUuid) {
        findActiveMovie(movieUuid);

        return reviewRepository
                .findAllByMovieUuidAndDeletedFalseOrderByCreatedAtDesc(movieUuid)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    /**
     * Returns all active reviews created by the authenticated user.
     *
     * <p>The reviews are returned from newest to oldest.</p>
     *
     * @return the authenticated user's active reviews
     */
    @Override
    public List<ReviewResponse> findMyReviews() {
        User currentUser = currentUserService.getCurrentUser();

        return reviewRepository
                .findAllByUserUuidAndDeletedFalseOrderByCreatedAtDesc(
                        currentUser.getUuid()
                )
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    /**
     * Creates a new review for a movie.
     *
     * <p>The authenticated user must have an active watch log for the
     * requested movie and must not already have an active review for it.</p>
     *
     * @param request the review creation request
     * @return the newly created review
     * @throws MovieNotFoundException   if the movie does not exist or has
     *                                  been soft deleted
     * @throws InvalidReviewException   if both the rating and review text
     *                                  are empty
     * @throws WatchLogRequiredException if the user has not logged the movie
     *                                   as watched
     * @throws DuplicateReviewException if the user already has an active
     *                                  review for the movie
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
     * Updates an existing review.
     *
     * <p>Both review fields are replaced by the values included in the
     * update request. The resulting review must still contain either a
     * rating or non-blank review text.</p>
     *
     * @param reviewUuid the UUID of the review
     * @param request    the review update request
     * @return the updated review
     * @throws ReviewNotFoundException     if the review does not exist or
     *                                     has been soft deleted
     * @throws ReviewAccessDeniedException if the review belongs to another
     *                                     user
     * @throws InvalidReviewException      if both resulting fields are empty
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
     * Soft deletes an existing review.
     *
     * <p>The operation does not physically remove the review from the
     * database. Instead, it marks the review as deleted using the common
     * soft-deletion mechanism inherited from {@code AbstractEntity}.</p>
     *
     * @param reviewUuid the UUID of the review
     * @throws ReviewNotFoundException     if the review does not exist or
     *                                     has been soft deleted
     * @throws ReviewAccessDeniedException if the review belongs to another
     *                                     user
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
            throw new WatchLogRequiredException();
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
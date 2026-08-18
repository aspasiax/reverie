package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.Review;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.review.CreateReviewRequest;
import io.github.aspasiax.reverie.dto.review.ReviewResponse;
import io.github.aspasiax.reverie.exception.DuplicateReviewException;
import io.github.aspasiax.reverie.exception.InvalidReviewException;
import io.github.aspasiax.reverie.exception.WatchLogRequiredException;
import io.github.aspasiax.reverie.mapper.ReviewMapper;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.ReviewRepository;
import io.github.aspasiax.reverie.repository.UserRepository;
import io.github.aspasiax.reverie.repository.WatchLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks the rules a review has to satisfy before it can exist.
 *
 * <p>Three of them are the reason this service has a service layer at all:
 * a review needs a viewing behind it, a user holds one review per film, and
 * a review has to say something.</p>
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private WatchLogRepository watchLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ICurrentUserService currentUserService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private final UUID userUuid = UUID.randomUUID();
    private final UUID movieUuid = UUID.randomUUID();

    /** The account the request is made by. */
    private User signedInUser() {
        User user = new User();
        user.setUuid(userUuid);
        return user;
    }

    /** The film being reviewed. */
    private Movie film() {
        Movie movie = new Movie();
        movie.setUuid(movieUuid);
        return movie;
    }

    @Test
    @DisplayName("a review is refused for a film that was never logged as watched")
    void createRefusesAReviewWithoutAWatchLog() {
        when(currentUserService.getCurrentUser()).thenReturn(signedInUser());
        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.of(film()));
        when(watchLogRepository
                .existsByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid))
                .thenReturn(false);

        CreateReviewRequest request =
                new CreateReviewRequest(movieUuid, 8, "Worth the running time.");

        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(WatchLogRequiredException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("a second review for the same film is refused")
    void createRefusesASecondReview() {
        when(currentUserService.getCurrentUser()).thenReturn(signedInUser());
        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.of(film()));
        when(watchLogRepository
                .existsByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid))
                .thenReturn(true);
        when(reviewRepository
                .existsByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid))
                .thenReturn(true);

        CreateReviewRequest request =
                new CreateReviewRequest(movieUuid, 8, "Worth the running time.");

        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(DuplicateReviewException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("a review with neither a rating nor any words is refused")
    void createRefusesAnEmptyReview() {
        when(currentUserService.getCurrentUser()).thenReturn(signedInUser());
        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.of(film()));

        /*
         * Blank text counts as no text, which is why this is rejected rather
         * than stored as a review saying "   ".
         */
        CreateReviewRequest request = new CreateReviewRequest(movieUuid, null, "   ");

        assertThatThrownBy(() -> reviewService.create(request))
                .isInstanceOf(InvalidReviewException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("a rating alone is a review")
    void createAcceptsARatingWithoutWords() {
        when(currentUserService.getCurrentUser()).thenReturn(signedInUser());
        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.of(film()));
        when(watchLogRepository
                .existsByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid))
                .thenReturn(true);
        when(reviewRepository
                .existsByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid))
                .thenReturn(false);
        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(reviewMapper.toResponse(any(Review.class))).thenReturn(
                new ReviewResponse(
                        UUID.randomUUID(), userUuid, "alex", movieUuid, "Arrival",
                        null, 8, null, Instant.now(), Instant.now()
                )
        );

        CreateReviewRequest request = new CreateReviewRequest(movieUuid, 8, null);

        ReviewResponse response = reviewService.create(request);

        assertThat(response.rating()).isEqualTo(8);
        assertThat(response.reviewText()).isNull();

        verify(reviewRepository).save(any(Review.class));
    }
}
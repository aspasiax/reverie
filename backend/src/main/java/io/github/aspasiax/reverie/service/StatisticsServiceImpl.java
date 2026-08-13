package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.statistics.HighlightResponse;
import io.github.aspasiax.reverie.dto.statistics.OverviewResponse;
import io.github.aspasiax.reverie.dto.user.UserStatisticsResponse;
import io.github.aspasiax.reverie.exception.UserNotFoundException;
import io.github.aspasiax.reverie.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link IStatisticsService}.
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements IStatisticsService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final WatchLogRepository watchLogRepository;
    private final WatchlistEntryRepository watchlistEntryRepository;
    private final ReviewRepository reviewRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics(UUID uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));

        /*
         * Only the first genre is wanted, but a query cannot return one row
         * without being told how many to return.
         */
        List<String> favouriteGenres = watchLogRepository
                .findGenreNamesByViewings(uuid, PageRequest.of(0, 1));

        return new UserStatisticsResponse(
                watchLogRepository.countDistinctMoviesWatchedBy(uuid),
                watchLogRepository.countByUserUuidAndDeletedFalse(uuid),
                reviewRepository.countByUserUuidAndDeletedFalse(uuid),
                reviewRepository.findAverageRatingGivenBy(uuid),
                favouriteGenres.isEmpty() ? null : favouriteGenres.getFirst()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public OverviewResponse getOverview() {
        /*
         * Both leaders are asked for as a page of one, because a query
         * cannot return a single row without being told how many to return.
         */
        Pageable first = PageRequest.of(0, 1);

        List<Movie> mostWatched = movieRepository.findMostWatched(first);
        List<User> mostActive = userRepository.findMostActive(first);

        return new OverviewResponse(
                userRepository.count(),
                userRepository.countByEnabledFalse(),
                movieRepository.countByDeletedFalseAndPublishedTrue(),
                movieRepository.countByDeletedFalseAndPublishedFalse(),
                movieRepository.countByDeletedTrue(),
                genreRepository.countByDeletedFalse(),
                reviewRepository.countByDeletedFalse(),
                watchLogRepository.countByDeletedFalse(),
                watchlistEntryRepository.countByDeletedFalse(),
                mostWatched.isEmpty() ? null : highlight(
                        mostWatched.getFirst().getTitle(),
                        mostWatched.getFirst().getWatchCount()
                ),
                mostActive.isEmpty() ? null : highlight(
                        mostActive.getFirst().getDisplayName(),
                        watchLogRepository.countByUserUuidAndDeletedFalse(
                                mostActive.getFirst().getUuid()
                        )
                )
        );
    }

    /**
     * Turns a leading row into a highlight, or into nothing when the count
     * behind it is zero.
     *
     * <p>The queries return whoever comes first even when nobody has done
     * anything, and calling a film with no viewings the most watched one
     * would be true only in the least useful sense.</p>
     *
     * @param name  what stands out
     * @param count how many times it happened
     * @return the highlight, or {@code null} when there is nothing to show
     */
    private HighlightResponse highlight(String name, long count) {
        return count == 0 ? null : new HighlightResponse(name, count);
    }
}
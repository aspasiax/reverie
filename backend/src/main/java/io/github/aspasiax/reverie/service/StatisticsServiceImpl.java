package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.user.UserStatisticsResponse;
import io.github.aspasiax.reverie.exception.UserNotFoundException;
import io.github.aspasiax.reverie.repository.ReviewRepository;
import io.github.aspasiax.reverie.repository.UserRepository;
import io.github.aspasiax.reverie.repository.WatchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    private final WatchLogRepository watchLogRepository;
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
}
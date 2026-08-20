package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.dto.statistics.OverviewResponse;
import io.github.aspasiax.reverie.repository.GenreRepository;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.ReviewRepository;
import io.github.aspasiax.reverie.repository.UserRepository;
import io.github.aspasiax.reverie.repository.WatchLogRepository;
import io.github.aspasiax.reverie.repository.WatchlistEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Checks that the overview declines to name a leader when there is none.
 *
 * <p>The queries behind the two highlights are ordered lists cut down to a
 * single row, so they hand back whichever film or reader happens to sort
 * first even when nobody has watched or written anything. Reporting that
 * row as the most watched film would be true only in the least useful
 * sense, and an administrator opening a fresh installation would be told
 * that a film nobody has seen is leading.</p>
 *
 * <p>The demonstration dataset can never reach this state, which is exactly
 * why it is worth a test.</p>
 */
@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private WatchLogRepository watchLogRepository;

    @Mock
    private WatchlistEntryRepository watchlistEntryRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    @DisplayName("a catalogue nobody has watched has no leading film or reader")
    void overviewNamesNoLeaderWhenNothingHasHappened() {
        /*
         * The film is mocked rather than built: its viewing count is derived
         * by the database and the entity deliberately offers no setter for
         * it, which is the whole reason a zero can arrive here at all.
         */
        Movie unwatched = mock(Movie.class);
        when(unwatched.getWatchCount()).thenReturn(0L);

        when(movieRepository.findMostWatched(any(Pageable.class)))
                .thenReturn(List.of(unwatched));
        when(userRepository.findMostActive(any(Pageable.class)))
                .thenReturn(List.of());

        OverviewResponse overview = statisticsService.getOverview();

        /*
         * Two different paths to the same answer: a film that exists but was
         * never watched, and no reader at all.
         */
        assertThat(overview.mostWatchedFilm()).isNull();
        assertThat(overview.mostActiveUser()).isNull();
    }
}
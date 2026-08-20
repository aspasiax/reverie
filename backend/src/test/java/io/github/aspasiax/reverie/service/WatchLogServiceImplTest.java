package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.domain.WatchLog;
import io.github.aspasiax.reverie.dto.watchlog.CreateWatchLogRequest;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import io.github.aspasiax.reverie.mapper.WatchLogMapper;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.WatchLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks what recording a viewing does besides recording it.
 *
 * <p>Watching a film fulfils the intention to watch it, so the entry has to
 * leave the watchlist. The removal itself belongs to the watchlist service
 * and is tested there; what is tested here is that this service still asks
 * for it. Nothing else would notice if the call disappeared: the watchlist
 * tests exercise that service directly, and a film left on the list after
 * being watched breaks no constraint.</p>
 */
@ExtendWith(MockitoExtension.class)
class WatchLogServiceImplTest {

    @Mock
    private WatchLogRepository watchLogRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private WatchLogMapper watchLogMapper;

    @Mock
    private ICurrentUserService currentUserService;

    @Mock
    private IWatchlistService watchlistService;

    @InjectMocks
    private WatchLogServiceImpl watchLogService;

    private final UUID userUuid = UUID.randomUUID();

    private final UUID movieUuid = UUID.randomUUID();

    /** The account recording the viewing. */
    private User viewer() {
        User user = new User();
        user.setUuid(userUuid);
        return user;
    }

    /** The film being recorded. */
    private Movie film() {
        Movie movie = new Movie();
        movie.setUuid(movieUuid);
        movie.setTitle("Arrival");
        return movie;
    }

    @Test
    @DisplayName("recording a viewing takes the film off the watchlist")
    void createClearsTheWatchlistEntry() {
        when(currentUserService.getCurrentUser()).thenReturn(viewer());
        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.of(film()));
        when(watchLogRepository.save(any(WatchLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        watchLogService.create(
                new CreateWatchLogRequest(movieUuid, LocalDate.of(2026, 3, 15))
        );

        verify(watchlistService).removeForWatchedMovie(userUuid, movieUuid);
    }

    @Test
    @DisplayName("a viewing of a film that is not in the catalogue is refused")
    void createRefusesAnUnknownFilm() {
        when(currentUserService.getCurrentUser()).thenReturn(viewer());
        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> watchLogService.create(
                new CreateWatchLogRequest(movieUuid, null)
        )).isInstanceOf(MovieNotFoundException.class);

        /*
         * Nothing is recorded and the watchlist is left alone. A viewing
         * that was refused must not fulfil an intention.
         */
        verify(watchLogRepository, never()).save(any());
        verifyNoInteractions(watchlistService);
    }
}
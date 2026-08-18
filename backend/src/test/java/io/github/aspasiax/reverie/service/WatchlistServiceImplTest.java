package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.domain.WatchlistEntry;
import io.github.aspasiax.reverie.dto.watchlist.CreateWatchlistEntryRequest;
import io.github.aspasiax.reverie.exception.DuplicateWatchlistEntryException;
import io.github.aspasiax.reverie.mapper.WatchlistEntryMapper;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.WatchlistEntryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks that a film is held on a watchlist once, and that clearing an
 * entry which was never there is not an error.
 */
@ExtendWith(MockitoExtension.class)
class WatchlistServiceImplTest {

    @Mock
    private WatchlistEntryRepository watchlistEntryRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private WatchlistEntryMapper watchlistEntryMapper;

    @Mock
    private ICurrentUserService currentUserService;

    @InjectMocks
    private WatchlistServiceImpl watchlistService;

    private final UUID userUuid = UUID.randomUUID();
    private final UUID movieUuid = UUID.randomUUID();

    /** The account making the request. */
    private User signedInUser() {
        User user = new User();
        user.setUuid(userUuid);
        return user;
    }

    /** The film being added. */
    private Movie film() {
        Movie movie = new Movie();
        movie.setUuid(movieUuid);
        return movie;
    }

    @Test
    @DisplayName("the same film cannot be added to a watchlist twice")
    void addRefusesAFilmAlreadyOnTheList() {
        when(currentUserService.getCurrentUser()).thenReturn(signedInUser());
        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.of(film()));
        when(watchlistEntryRepository
                .findByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid))
                .thenReturn(Optional.of(new WatchlistEntry()));

        CreateWatchlistEntryRequest request = new CreateWatchlistEntryRequest(movieUuid);

        assertThatThrownBy(() -> watchlistService.add(request))
                .isInstanceOf(DuplicateWatchlistEntryException.class);

        verify(watchlistEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("watching a film that was never on the list is not an error")
    void removeForWatchedMovieIgnoresAFilmThatWasNotListed() {
        when(watchlistEntryRepository
                .findByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid))
                .thenReturn(Optional.empty());

        /*
         * This runs on every recorded viewing, and most films were never on
         * anybody list. Turning that into an exception would make logging a
         * viewing fail for the ordinary case.
         */
        assertThatCode(() -> watchlistService.removeForWatchedMovie(userUuid, movieUuid))
                .doesNotThrowAnyException();

        verify(watchlistEntryRepository, never()).save(any());
    }

    @Test
    @DisplayName("watching a film that was on the list removes the entry")
    void removeForWatchedMovieRemovesTheEntry() {
        WatchlistEntry entry = new WatchlistEntry();

        when(watchlistEntryRepository
                .findByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid))
                .thenReturn(Optional.of(entry));

        watchlistService.removeForWatchedMovie(userUuid, movieUuid);

        assertThat(entry.isDeleted()).isTrue();

        verify(watchlistEntryRepository).save(entry);
    }
}
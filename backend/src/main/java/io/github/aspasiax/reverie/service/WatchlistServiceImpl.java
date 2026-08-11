package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.domain.WatchlistEntry;
import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.watchlist.CreateWatchlistEntryRequest;
import io.github.aspasiax.reverie.dto.watchlist.WatchlistEntryResponse;
import io.github.aspasiax.reverie.exception.DuplicateWatchlistEntryException;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import io.github.aspasiax.reverie.exception.WatchlistEntryNotFoundException;
import io.github.aspasiax.reverie.mapper.WatchlistEntryMapper;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.WatchlistEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Default implementation of {@link IWatchlistService}.
 *
 * <p>
 * Handles retrieval, addition and removal of the films the authenticated
 * user intends to watch.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements IWatchlistService {

    private final WatchlistEntryRepository watchlistEntryRepository;
    private final MovieRepository movieRepository;
    private final WatchlistEntryMapper watchlistEntryMapper;
    private final ICurrentUserService currentUserService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<WatchlistEntryResponse> findMyWatchlist(Pageable pageable) {
        User currentUser = currentUserService.getCurrentUser();

        Page<WatchlistEntryResponse> page = watchlistEntryRepository
                .findAllByUserUuidAndDeletedFalse(currentUser.getUuid(), pageable)
                .map(watchlistEntryMapper::toResponse);

        return PageResponse.from(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public WatchlistEntryResponse add(CreateWatchlistEntryRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Movie movie = movieRepository
                .findByUuidAndDeletedFalse(request.movieUuid())
                .orElseThrow(() ->
                        new MovieNotFoundException(request.movieUuid()));

        /*
         * The database would refuse a second active entry anyway, through
         * the partial unique index. Checking here turns that into a clear
         * message instead of a generic integrity violation.
         */
        watchlistEntryRepository
                .findByUserUuidAndMovieUuidAndDeletedFalse(
                        currentUser.getUuid(),
                        movie.getUuid()
                )
                .ifPresent(entry -> {
                    throw new DuplicateWatchlistEntryException();
                });

        WatchlistEntry entry = new WatchlistEntry();
        entry.setUser(currentUser);
        entry.setMovie(movie);

        WatchlistEntry savedEntry = watchlistEntryRepository.save(entry);

        return watchlistEntryMapper.toResponse(savedEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void remove(UUID uuid) {
        User currentUser = currentUserService.getCurrentUser();

        WatchlistEntry entry = watchlistEntryRepository
                .findByUuidAndDeletedFalse(uuid)
                .orElseThrow(() -> new WatchlistEntryNotFoundException(uuid));

        if (!entry.getUser().equals(currentUser)) {
            throw new AccessDeniedException(
                    "You are not allowed to modify this watchlist."
            );
        }

        entry.softDelete();

        watchlistEntryRepository.save(entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void removeForWatchedMovie(UUID userUuid, UUID movieUuid) {
        watchlistEntryRepository
                .findByUserUuidAndMovieUuidAndDeletedFalse(userUuid, movieUuid)
                .ifPresent(entry -> {
                    entry.softDelete();
                    watchlistEntryRepository.save(entry);
                });
    }
}
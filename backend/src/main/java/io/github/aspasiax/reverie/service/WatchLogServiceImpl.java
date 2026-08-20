package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.domain.WatchLog;
import io.github.aspasiax.reverie.dto.common.PageResponse;
import io.github.aspasiax.reverie.dto.watchlog.CreateWatchLogRequest;
import io.github.aspasiax.reverie.dto.watchlog.UpdateWatchLogRequest;
import io.github.aspasiax.reverie.dto.watchlog.WatchLogResponse;
import io.github.aspasiax.reverie.exception.MovieNotFoundException;
import io.github.aspasiax.reverie.exception.WatchLogNotFoundException;
import io.github.aspasiax.reverie.mapper.WatchLogMapper;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.WatchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Default implementation of {@link IWatchLogService}.
 *
 * <p>
 * Handles retrieval, creation and soft deletion of watch logs
 * belonging to the authenticated user.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class WatchLogServiceImpl implements IWatchLogService {

    private final WatchLogRepository watchLogRepository;
    private final MovieRepository movieRepository;
    private final WatchLogMapper watchLogMapper;
    private final ICurrentUserService currentUserService;
    private final IWatchlistService watchlistService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<WatchLogResponse> findMyWatchLogs(Pageable pageable) {
        User currentUser = currentUserService.getCurrentUser();

        Page<WatchLogResponse> page = watchLogRepository
                .findAllByUserUuidAndDeletedFalse(currentUser.getUuid(), pageable)
                .map(watchLogMapper::toResponse);

        return PageResponse.from(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public WatchLogResponse create(CreateWatchLogRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Movie movie = movieRepository
                .findByUuidAndDeletedFalse(request.movieUuid())
                .orElseThrow(() ->
                        new MovieNotFoundException(request.movieUuid()));

        WatchLog watchLog = new WatchLog();
        watchLog.setUser(currentUser);
        watchLog.setMovie(movie);
        watchLog.setWatchedAt(request.watchedAt());

        WatchLog savedWatchLog = watchLogRepository.save(watchLog);

        /*
         * Both calls run inside this transaction, so a film is never left
         * both watched and still waiting to be watched.
         */
        watchlistService.removeForWatchedMovie(
                currentUser.getUuid(),
                movie.getUuid()
        );

        return watchLogMapper.toResponse(savedWatchLog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public WatchLogResponse update(
            UUID uuid,
            UpdateWatchLogRequest request
    ) {
        User currentUser = currentUserService.getCurrentUser();

        WatchLog watchLog = findActiveWatchLog(uuid);

        if (!watchLog.getUser().equals(currentUser)) {
            throw new AccessDeniedException(
                    "You are not allowed to modify this watch log."
            );
        }

        watchLog.setWatchedAt(request.watchedAt());

        WatchLog updatedWatchLog = watchLogRepository.save(watchLog);

        return watchLogMapper.toResponse(updatedWatchLog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delete(UUID uuid) {

        User currentUser = currentUserService.getCurrentUser();

        WatchLog watchLog = findActiveWatchLog(uuid);

        if (!watchLog.getUser().equals(currentUser)) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this watch log."
            );
        }

        watchLog.softDelete();

        watchLogRepository.save(watchLog);
    }

    /**
     * Finds an active watch log.
     *
     * @param uuid the public watch log UUID
     * @return the matching active watch log
     * @throws WatchLogNotFoundException if the watch log does not exist
     */
    private WatchLog findActiveWatchLog(UUID uuid) {

        return watchLogRepository
                .findByUuidAndDeletedFalse(uuid)
                .orElseThrow(() ->
                        new WatchLogNotFoundException(uuid));
    }

}
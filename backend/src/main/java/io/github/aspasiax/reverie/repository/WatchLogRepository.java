package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.WatchLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to watch logs.
 */
public interface WatchLogRepository extends JpaRepository<WatchLog, Long> {

    /**
     * Returns a page of active watch logs belonging to a user.
     *
     * @param userUuid the public UUID of the user
     * @param pageable the requested page and sort order
     * @return a page of the user's active watch logs
     */
    Page<WatchLog> findAllByUserUuidAndDeletedFalse(UUID userUuid, Pageable pageable);

    /**
     * Finds an active watch log by its public UUID.
     *
     * @param uuid the public watch log UUID
     * @return the matching active watch log, if found
     */
    Optional<WatchLog> findByUuidAndDeletedFalse(UUID uuid);

    boolean existsByUserUuidAndMovieUuidAndDeletedFalse(
            UUID userUuid,
            UUID movieUuid
    );

}
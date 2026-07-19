package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.WatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to watch logs.
 */
public interface WatchLogRepository extends JpaRepository<WatchLog, Long> {

    /**
     * Retrieves all active watch logs belonging to a user,
     * ordered from newest to oldest.
     *
     * @param userUuid the public UUID of the user
     * @return the user's active watch logs ordered by creation timestamp
     */
    List<WatchLog> findAllByUserUuidAndDeletedFalseOrderByCreatedAtDesc(
            UUID userUuid
    );

    /**
     * Finds an active watch log by its public UUID.
     *
     * @param uuid the public watch log UUID
     * @return the matching active watch log, if found
     */
    Optional<WatchLog> findByUuidAndDeletedFalse(UUID uuid);

}
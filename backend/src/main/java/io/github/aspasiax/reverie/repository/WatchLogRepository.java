package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.WatchLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    /**
     * Counts the distinct films a user has logged.
     *
     * <p>Different from the number of viewings: a film watched three times
     * counts once here and three times there.</p>
     *
     * @param userUuid the public user identifier
     * @return how many different films the user has watched
     */
    @Query("""
            SELECT COUNT(DISTINCT w.movie)
            FROM WatchLog w
            WHERE w.user.uuid = :userUuid AND w.deleted = FALSE
            """)
    long countDistinctMoviesWatchedBy(@Param("userUuid") UUID userUuid);

    /**
     * Counts the viewings a user has recorded.
     *
     * @param userUuid the public user identifier
     * @return how many viewings the user has recorded
     */
    long countByUserUuidAndDeletedFalse(UUID userUuid);

    /**
     * Returns the genres a user watches, most watched first.
     *
     * <p>A film belongs to several genres, so one viewing counts towards
     * each of them. The caller asks for as many as it needs, which in
     * practice is one.</p>
     *
     * @param userUuid the public user identifier
     * @param pageable how many to return
     * @return genre names ordered by how often the user watches them
     */
    @Query("""
            SELECT g.name
            FROM WatchLog w
            JOIN w.movie m
            JOIN m.genres g
            WHERE w.user.uuid = :userUuid AND w.deleted = FALSE
            GROUP BY g.name
            ORDER BY COUNT(g) DESC, g.name ASC
            """)
    List<String> findGenreNamesByViewings(
            @Param("userUuid") UUID userUuid,
            Pageable pageable
    );

    /**
     * Counts the records that have not been deleted.
     *
     * @return how many active records exist
     */
    long countByDeletedFalse();
}
package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.WatchlistEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to watchlist
 * entries.
 */
public interface WatchlistEntryRepository extends JpaRepository<WatchlistEntry, Long> {

    /**
     * Returns a page of the entries belonging to a user.
     *
     * @param userUuid the public user identifier
     * @param pageable the requested page and sort order
     * @return a page of the user's active entries
     */
    Page<WatchlistEntry> findAllByUserUuidAndDeletedFalse(
            UUID userUuid,
            Pageable pageable
    );

    /**
     * Finds an active entry by its public UUID.
     *
     * @param uuid the entry UUID
     * @return the matching active entry, if found
     */
    Optional<WatchlistEntry> findByUuidAndDeletedFalse(UUID uuid);

    /**
     * Finds the active entry a user holds for a film.
     *
     * <p>This is the lookup behind both rules of the feature: it rejects a
     * film that is already on the list, and it finds the entry to remove
     * once the film has been watched.</p>
     *
     * @param userUuid  the public user identifier
     * @param movieUuid the public movie identifier
     * @return the matching active entry, if found
     */
    Optional<WatchlistEntry> findByUserUuidAndMovieUuidAndDeletedFalse(
            UUID userUuid,
            UUID movieUuid
    );

    /**
     * Counts the records that have not been deleted.
     *
     * @return how many active records exist
     */
    long countByDeletedFalse();
}
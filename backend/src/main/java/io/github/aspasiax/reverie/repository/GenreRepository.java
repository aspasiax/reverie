package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to genres.
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Returns all genres that have not been soft deleted.
     *
     * @return a list containing all active genres
     */
    List<Genre> findAllByDeletedFalseOrderByNameAsc();

    /**
     * Finds an active genre by its public UUID.
     *
     * @param uuid the genre UUID
     * @return the matching active genre, if found
     */
    Optional<Genre> findByUuidAndDeletedFalse(UUID uuid);

    /**
     * Finds an active genre by its name, ignoring letter case.
     *
     * <p>Soft-deleted genres are excluded because the database uniqueness
     * constraint only applies to active records.</p>
     *
     * @param name the genre name
     * @return the matching active genre, if found
     */
    Optional<Genre> findByNameIgnoreCaseAndDeletedFalse(String name);

    /**
     * Checks whether an active genre already uses the given name,
     * ignoring letter case.
     *
     * <p>Soft-deleted genres are excluded because the database uniqueness
     * constraint only applies to active records.</p>
     *
     * @param name the genre name
     * @return {@code true} if an active genre uses the name
     */
    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
}
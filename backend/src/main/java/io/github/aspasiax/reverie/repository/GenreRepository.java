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
     * Finds a genre by its name, ignoring letter case.
     *
     * <p>Soft-deleted genres are included because the database
     * uniqueness constraint also includes those records.</p>
     *
     * @param name the genre name
     * @return the matching genre, if found
     */
    Optional<Genre> findByNameIgnoreCase(String name);

    /**
     * Checks whether a genre name is already used, ignoring letter case.
     *
     * <p>Soft-deleted genres are included because the database
     * uniqueness constraint also includes those records.</p>
     *
     * @param name the genre name
     * @return {@code true} if the name already exists
     */
    boolean existsByNameIgnoreCase(String name);
}
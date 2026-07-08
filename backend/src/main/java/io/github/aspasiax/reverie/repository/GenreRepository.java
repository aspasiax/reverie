package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to genres.
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Finds a genre by its public UUID.
     *
     * @param uuid the genre UUID
     * @return the matching genre, if found
     */
    Optional<Genre> findByUuid(UUID uuid);

    /**
     * Finds a genre by its unique name.
     *
     * @param name the genre name
     * @return the matching genre, if found
     */
    Optional<Genre> findByName(String name);

    /**
     * Checks whether a genre with the given name already exists.
     *
     * @param name the genre name
     * @return {@code true} if a genre exists with the given name
     */
    boolean existsByName(String name);
}
package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link Review} entities.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Returns a page of active reviews written for a specific movie.
     *
     * @param movieUuid the UUID of the movie
     * @param pageable  the requested page and sort order
     * @return a page of active reviews
     */
    Page<Review> findAllByMovieUuidAndDeletedFalse(UUID movieUuid, Pageable pageable);

    /**
     * Returns a page of active reviews written by a specific user.
     *
     * @param userUuid the UUID of the user
     * @param pageable the requested page and sort order
     * @return a page of active reviews
     */
    Page<Review> findAllByUserUuidAndDeletedFalse(UUID userUuid, Pageable pageable);

    /**
     * Finds an active review by its public UUID.
     *
     * @param uuid the review UUID
     * @return an Optional containing the review if found
     */
    Optional<Review> findByUuidAndDeletedFalse(UUID uuid);

    /**
     * Checks whether an active review already exists for a user and movie.
     *
     * @param userUuid the user UUID
     * @param movieUuid the movie UUID
     * @return {@code true} if an active review exists
     */
    boolean existsByUserUuidAndMovieUuidAndDeletedFalse(UUID userUuid, UUID movieUuid);
}
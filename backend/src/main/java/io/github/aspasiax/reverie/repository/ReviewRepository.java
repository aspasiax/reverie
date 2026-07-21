package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link Review} entities.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Returns all active reviews for a specific movie ordered by creation date (newest first).
     *
     * @param movieUuid the UUID of the movie
     * @return a list of active reviews
     */
    List<Review> findAllByMovieUuidAndDeletedFalseOrderByCreatedAtDesc(UUID movieUuid);

    /**
     * Returns all active reviews created by a specific user ordered by creation date (newest first).
     *
     * @param userUuid the UUID of the user
     * @return a list of active reviews
     */
    List<Review> findAllByUserUuidAndDeletedFalseOrderByCreatedAtDesc(UUID userUuid);

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
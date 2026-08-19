package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Counts the reviews a user has written.
     *
     * @param userUuid the public user identifier
     * @return how many reviews the user has written
     */
    long countByUserUuidAndDeletedFalse(UUID userUuid);

    /**
     * Returns the average score a user gives.
     *
     * <p>Reviews without a score are ignored rather than counted as zero,
     * so a user who only writes text has no average at all.</p>
     *
     * @param userUuid the public user identifier
     * @return the average rating, or {@code null} when the user rates nothing
     */
    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.user.uuid = :userUuid AND r.deleted = FALSE
            """)
    Double findAverageRatingGivenBy(@Param("userUuid") UUID userUuid);

    /**
     * Returns a page of active reviews from across the catalogue.
     *
     * <p>Reviews of unpublished films are left out. A draft does not appear
     * in the catalogue, so a review of one must not appear either: following
     * it would lead to a film the reader is not allowed to open.</p>
     *
     * @param pageable the requested page and sort order
     * @return a page of reviews written for published films
     */
    @Query("""
            SELECT r FROM Review r
            WHERE r.deleted = FALSE
              AND r.movie.deleted = FALSE
              AND r.movie.published = TRUE
            """)
    Page<Review> findAllPublished(Pageable pageable);

    /**
     * Counts the records that have not been deleted.
     *
     * @return how many active records exist
     */
    long countByDeletedFalse();
}
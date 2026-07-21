package io.github.aspasiax.reverie.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a review submitted by a user for a movie.
 *
 * <p>A review may contain a rating, review text, or both. The user must have
 * at least one active watch log for the associated movie before creating a
 * review. This business rule is enforced by the service layer.</p>
 *
 * <p>Each user may have only one active review per movie. This restriction is
 * enforced at the database level through a partial unique index.</p>
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review extends AbstractEntity {

    /**
     * The database identifier of the review.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The public unique identifier of the review.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * The user who created the review.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The movie associated with the review.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    /**
     * The optional rating assigned to the movie.
     *
     * <p>When present, the value must be between 1 and 10.</p>
     */
    @Column
    private Integer rating;

    /**
     * The optional written review.
     */
    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    /**
     * Initializes the public UUID before the entity is first persisted.
     */
    @PrePersist
    protected void initializeUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    /**
     * Compares reviews using their immutable public UUID.
     *
     * @param o the object to compare with
     * @return {@code true} when both reviews have the same non-null UUID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Review review)) {
            return false;
        }

        return uuid != null && uuid.equals(review.uuid);
    }

    /**
     * Generates a hash code based on the review UUID.
     *
     * @return the hash code of the UUID
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
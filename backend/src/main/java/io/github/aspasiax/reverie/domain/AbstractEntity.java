package io.github.aspasiax.reverie.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Base entity for all persistent domain objects in the Reverie application.
 * <p>
 * Provides automatic auditing information (creation and update timestamps)
 * as well as support for soft deletion.
 *
 * <p>All entities in the domain layer should extend this class.
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    /**
     * Timestamp indicating when the entity was created.
     * Automatically populated by Spring Data JPA.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private Instant createdAt;

    /**
     * Timestamp indicating the last modification of the entity.
     * Automatically updated whenever the entity changes.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMPTZ")
    private Instant updatedAt;

    /**
     * Indicates whether the entity has been soft deleted.
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * Timestamp indicating when the entity was soft deleted.
     * Remains {@code null} while the entity is active.
     */
    @Column(name = "deleted_at", columnDefinition = "TIMESTAMPTZ")
    private Instant deletedAt;

    /**
     * Marks the entity as deleted without removing it from the database.
     */
    public void softDelete() {
        this.deleted = true;
        this.deletedAt = Instant.now();
    }

    /**
     * Restores a previously soft deleted entity.
     */
    public void restoreFromSoftDelete() {
        this.deleted = false;
        this.deletedAt = null;
    }
}
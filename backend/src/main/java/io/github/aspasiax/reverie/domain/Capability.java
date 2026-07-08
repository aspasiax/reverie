package io.github.aspasiax.reverie.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a fine-grained permission in the Reverie application.
 *
 * <p>Capabilities may be assigned to roles in order to describe
 * what actions users with a specific role are allowed to perform.</p>
 */
@Entity
@Table(name = "capabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capability extends AbstractEntity {

    /**
     * Internal database identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public unique identifier used by the API instead of the internal id.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * Unique capability name, such as MOVIE_CREATE or USER_MANAGE.
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Optional description explaining what the capability allows.
     */
    @Size(max = 255)
    @Column(length = 255)
    private String description;

    /**
     * Generates a UUID before the entity is persisted.
     */
    @PrePersist
    public void initializeUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    /**
     * Two capabilities are considered equal if they share the same UUID.
     *
     * @param o the object to compare
     * @return {@code true} if both objects represent the same capability
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capability capability)) return false;
        return Objects.equals(uuid, capability.uuid);
    }

    /**
     * Computes the hash code based on the immutable UUID.
     *
     * @return the hash code of the capability
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
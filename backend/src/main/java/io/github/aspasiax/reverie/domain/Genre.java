package io.github.aspasiax.reverie.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a movie genre in the Reverie application.
 *
 * <p>Genres classify movies and improve browsing,
 * filtering and discovery throughout the platform.</p>
 */
@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre extends AbstractEntity {

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
     * Unique genre name displayed to users.
     */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Optional short description of the genre.
     */
    @Size(max = 500)
    @Column(length = 500)
    private String description;

    /**
     * Icon name used by the frontend.
     */
    @Size(max = 100)
    @Column(length = 100)
    private String icon;

    /**
     * Color value used by the frontend for genre badges.
     */
    @Size(max = 20)
    @Column(length = 20)
    private String color;

    /**
     * Movies associated with this genre.
     *
     * <p>This is the inverse side of the many-to-many relationship.
     * The owning side is defined in {@link Movie}.</p>
     */
    @Builder.Default
    @ManyToMany(mappedBy = "genres")
    private Set<Movie> movies = new HashSet<>();

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
     * Two genres are considered equal if they share the same UUID.
     *
     * @param o the object to compare
     * @return {@code true} if both objects represent the same genre
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre genre)) return false;
        return Objects.equals(uuid, genre.uuid);
    }

    /**
     * Computes the hash code based on the immutable UUID.
     *
     * @return the hash code of the genre
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
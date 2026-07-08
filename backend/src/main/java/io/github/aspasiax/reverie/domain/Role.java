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
 * Represents a security role in the Reverie application.
 *
 * <p>Roles define the general access level of a user, such as
 * {@code USER} or {@code ADMIN}. Capabilities may be added later
 * for more fine-grained permissions.</p>
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends AbstractEntity {

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
     * Unique role name, such as USER or ADMIN.
     */
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Optional description explaining the purpose of the role.
     */
    @Size(max = 255)
    @Column(length = 255)
    private String description;

    /**
     * Capabilities assigned to this role.
     *
     * <p>They describe the fine-grained permissions available to users
     * that have this role.</p>
     */
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "role_capabilities",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "capability_id")
    )
    private Set<Capability> capabilities = new HashSet<>();

    /**
     * Adds a capability to the role.
     *
     * @param capability the capability to add
     */
    public void addCapability(Capability capability) {
        capabilities.add(capability);
    }

    /**
     * Removes a capability from the role.
     *
     * @param capability the capability to remove
     */
    public void removeCapability(Capability capability) {
        capabilities.remove(capability);
    }

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
     * Two roles are considered equal if they share the same UUID.
     *
     * @param o the object to compare
     * @return {@code true} if both objects represent the same role
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return Objects.equals(uuid, role.uuid);
    }

    /**
     * Computes the hash code based on the immutable UUID.
     *
     * @return the hash code of the role
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
package io.github.aspasiax.reverie.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a registered user of the Reverie application.
 *
 * <p>A user can browse movies, write reviews, maintain a watchlist,
 * log watched movies and interact with the platform according to
 * the assigned role.</p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractEntity {

    /**
     * Internal database identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public unique identifier exposed by the API.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * Unique username used for login and public profile.
     */
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * User email address.
     */
    @Email
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Encrypted user password (BCrypt).
     */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /**
     * User's first name.
     */
    @Size(max = 100)
    @Column(length = 100)
    private String firstName;

    /**
     * User's last name.
     */
    @Size(max = 100)
    @Column(length = 100)
    private String lastName;

    /**
     * Optional short biography displayed on the user's profile.
     */
    @Size(max = 500)
    @Column(length = 500)
    private String bio;

    /**
     * URL pointing to the user's profile image.
     */
    @Size(max = 1024)
    @Column(length = 1024)
    private String profileImageUrl;

    /**
     * Indicates whether the account is active.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Security role assigned to the user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

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
     * Two users are considered equal if they share the same UUID.
     *
     * @param o the object to compare
     * @return {@code true} if both users represent the same entity
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(uuid, user.uuid);
    }

    /**
     * Computes the hash code based on the immutable UUID.
     *
     * @return the hash code of the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a registered user of the Reverie application.
 *
 * <p>A user can browse movies, write reviews, maintain a watchlist,
 * manage profile information and interact with the platform according
 * to the assigned role and capabilities.</p>
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
     * Public immutable identifier exposed by the API instead of
     * the internal database id.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * Unique public username used as the user's handle.
     */
    @NotBlank
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Unique email address used for authentication.
     */
    @Email
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * BCrypt-encoded user password.
     */
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Name displayed on the user's profile and alongside
     * user-generated content.
     *
     * <p>This value does not need to be the user's legal name.
     * A user may provide a full name, first name, nickname or
     * any other preferred display name.</p>
     */
    @NotBlank
    @Size(min = 2, max = 150)
    @Column(name = "display_name", nullable = false, length = 150)
    private String displayName;

    /**
     * Optional short biography displayed on the user's profile.
     */
    @Size(max = 500)
    @Column(length = 500)
    private String bio;

    /**
     * Optional URL pointing to the user's profile image.
     */
    @Size(max = 1024)
    @Column(name = "profile_image_url", length = 1024)
    private String profileImageUrl;

    /**
     * The film the user names as their favourite.
     *
     * <p>Optional, and restricted to films the user has already watched.
     * That rule is enforced by the service: it depends on the watch logs of
     * the account making the choice, which no column constraint can see.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favourite_movie_id")
    private Movie favouriteMovie;


    /**
     * Indicates whether the account is enabled and may authenticate.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Allows the account to sign in again.
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * Withdraws the account from use.
     *
     * <p>Nothing the account created is removed. It simply stops being able
     * to authenticate, which the JWT filter checks on every request.</p>
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * Security role assigned to the user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * Generates the public UUID before the entity is persisted.
     */
    @PrePersist
    public void initializeUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    /**
     * Two users are considered equal when they share the same UUID.
     *
     * @param o the object to compare
     * @return {@code true} if both objects represent the same user
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(uuid, user.uuid);
    }

    /**
     * Computes the hash code using the immutable public UUID.
     *
     * @return the hash code of the user
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
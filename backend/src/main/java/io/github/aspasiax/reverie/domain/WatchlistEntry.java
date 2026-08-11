package io.github.aspasiax.reverie.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entity representing a film a user intends to watch.
 *
 * <p>
 * Unlike a watch log, which records something that happened and may happen
 * again, an entry records an intention. An intention is held once, so a
 * user has at most one active entry per film.
 * </p>
 *
 * <p>
 * That rule is enforced by a partial unique index in the database rather
 * than here, because JPA cannot express an index that applies only to rows
 * which have not been soft deleted.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "watchlist_entries")
public class WatchlistEntry extends AbstractEntity {

    /**
     * Internal database primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public unique identifier exposed through the API.
     */
    @Column(
            name = "uuid",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID uuid;

    /**
     * User who intends to watch the movie.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_watchlist_entries_user")
    )
    private User user;

    /**
     * Movie the user intends to watch.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_watchlist_entries_movie")
    )
    private Movie movie;

    /**
     * Generates the public UUID before the entity is persisted.
     */
    @PrePersist
    protected void initializeUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    /**
     * Equality is based on the persistent public UUID.
     *
     * @param o object to compare with this entry
     * @return {@code true} when both entities have the same non-null UUID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof WatchlistEntry entry)) {
            return false;
        }

        return uuid != null && uuid.equals(entry.uuid);
    }

    /**
     * Uses a stable hash code compatible with Hibernate proxies and
     * generated identifiers.
     *
     * @return stable hash-code value
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
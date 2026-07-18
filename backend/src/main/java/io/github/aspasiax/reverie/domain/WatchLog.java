package io.github.aspasiax.reverie.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing a single movie viewing recorded by a user.
 *
 * <p>
 * A user may create multiple watch logs for the same movie, allowing
 * Reverie to keep track of rewatches as separate viewing events.
 * </p>
 *
 * <p>
 * The watch date is optional because users may wish to record a viewing
 * even when they do not remember the exact date.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "watch_logs",
        indexes = {
                @Index(
                        name = "idx_watch_logs_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_watch_logs_movie_id",
                        columnList = "movie_id"
                ),
                @Index(
                        name = "idx_watch_logs_watched_at",
                        columnList = "watched_at"
                )
        }
)
public class WatchLog extends AbstractEntity {

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
     * User who recorded the viewing.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_watch_logs_user")
    )
    private User user;

    /**
     * Movie associated with the viewing.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_watch_logs_movie")
    )
    private Movie movie;

    /**
     * Date on which the movie was watched.
     *
     * <p>
     * This value may be {@code null} when the user does not remember
     * the exact viewing date.
     * </p>
     */
    @Column(name = "watched_at")
    private LocalDate watchedAt;

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
     * @param o object to compare with this watch log
     * @return {@code true} when both entities have the same non-null UUID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof WatchLog watchLog)) {
            return false;
        }

        return uuid != null && uuid.equals(watchLog.uuid);
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
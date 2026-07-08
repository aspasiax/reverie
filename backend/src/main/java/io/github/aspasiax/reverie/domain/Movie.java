package io.github.aspasiax.reverie.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a movie available in the Reverie application.
 *
 * <p>A movie stores general information imported from TMDB or
 * manually created by an administrator.</p>
 */
@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie extends AbstractEntity {

    /**
     * Internal database identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public unique identifier used throughout the application.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * Display title of the movie.
     */
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Original title in the movie's original language.
     */
    @Size(max = 255)
    @Column(name = "original_title", length = 255)
    private String originalTitle;

    /**
     * Short plot summary.
     */
    @Column(columnDefinition = "TEXT")
    private String overview;

    /**
     * Official theatrical release date.
     */
    @Column(name = "release_date")
    private LocalDate releaseDate;

    /**
     * Runtime in minutes.
     */
    @Positive
    private Integer runtime;

    /**
     * URL of the movie poster.
     */
    @Size(max = 1024)
    @Column(name = "poster_url", length = 1024)
    private String posterUrl;

    /**
     * URL of the backdrop image.
     */
    @Size(max = 1024)
    @Column(name = "backdrop_url", length = 1024)
    private String backdropUrl;

    /**
     * Unique TMDB identifier used for synchronization.
     */
    @Positive
    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    /**
     * Determines whether the movie is visible to users.
     */
    @Column(nullable = false)
    private boolean published = false;

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
     * Publishes the movie, making it visible to users.
     */
    public void publish() {
        this.published = true;
    }

    /**
     * Hides the movie from users without deleting it.
     */
    public void unpublish() {
        this.published = false;
    }

    /**
     * Two movies are considered equal if they share the same UUID.
     *
     * @param o the object to compare
     * @return {@code true} if both objects represent the same movie
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie movie)) return false;
        return Objects.equals(uuid, movie.uuid);
    }

    /**
     * Computes the hash code based on the immutable UUID.
     *
     * @return the hash code of the movie
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
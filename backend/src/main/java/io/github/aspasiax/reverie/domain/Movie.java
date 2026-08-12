package io.github.aspasiax.reverie.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a movie available in the Reverie application.
 *
 * <p>A movie may be created manually by an administrator or imported
 * from an external movie service such as TMDB. External identifiers
 * are optional so that the application does not depend on a public API
 * for manually created movies.</p>
 *
 * <p>Movies may be published or unpublished. Only published movies
 * should normally be visible through the public API.</p>
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
     * Public immutable identifier used by the API instead of the
     * internal database id.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    /**
     * Main title displayed throughout the application.
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
     * Short summary describing the movie's plot.
     */
    @Column(columnDefinition = "TEXT")
    private String overview;

    /**
     * Official release date of the movie.
     */
    @Column(name = "release_date")
    private LocalDate releaseDate;

    /**
     * Runtime of the movie in minutes.
     */
    @Positive
    private Integer runtime;

    /**
     * ISO-style code representing the movie's original language,
     * such as {@code en}, {@code el} or {@code fr}.
     */
    @Size(max = 10)
    @Column(name = "original_language", length = 10)
    private String originalLanguage;

    /**
     * Relative poster image path, usually received from TMDB.
     *
     * <p>The complete image URL may be constructed in the service
     * or response-mapping layer.</p>
     */
    @Size(max = 1024)
    @Column(name = "poster_path", length = 1024)
    private String posterPath;

    /**
     * Relative backdrop image path, usually received from TMDB.
     *
     * <p>The complete image URL may be constructed in the service
     * or response-mapping layer.</p>
     */
    @Size(max = 1024)
    @Column(name = "backdrop_path", length = 1024)
    private String backdropPath;

    /**
     * Optional unique identifier assigned to the movie by TMDB.
     *
     * <p>This value remains {@code null} for movies created manually
     * without using the TMDB import functionality.</p>
     */
    @Positive
    @Column(name = "tmdb_id", unique = true)
    private Long tmdbId;

    /**
     * Optional unique identifier assigned to the movie by IMDb.
     */
    @Size(max = 20)
    @Column(name = "imdb_id", unique = true, length = 20)
    private String imdbId;

    /**
     * Indicates whether the movie is visible through the public API.
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean published = false;

    /**
     * Average of the ratings given to this movie.
     *
     * <p>Computed by the database on every read. Movies with no ratings
     * produce zero rather than nothing, so that ordering by rating places
     * them last instead of first, which is where a null would put them.
     * Whether the number means anything is told by {@link #ratingCount}.</p>
     */
    @Formula("""
            (SELECT COALESCE(AVG(r.rating), 0)
             FROM reviews r
             WHERE r.movie_id = id AND r.deleted = FALSE)
            """)
    @Setter(AccessLevel.NONE)
    private Double averageRating;

    /**
     * Number of ratings given to this movie.
     *
     * <p>A review may carry written text without a score, so this is not
     * the number of reviews. It is what says whether {@link #averageRating}
     * means anything: an average over no ratings is zero, and zero is not
     * a score anybody gave.</p>
     */
    @Formula("""
            (SELECT COUNT(r.rating)
             FROM reviews r
             WHERE r.movie_id = id AND r.deleted = FALSE)
            """)
    @Setter(AccessLevel.NONE)
    private Long ratingCount;

    /**
     * Number of recorded viewings of this movie.
     *
     * <p>A rewatch counts again, because the domain treats it as a separate
     * event. This is a count of viewings, not of viewers.</p>
     */
    @Formula("""
            (SELECT COUNT(*)
             FROM watch_logs w
             WHERE w.movie_id = id AND w.deleted = FALSE)
            """)
    @Setter(AccessLevel.NONE)
    private Long watchCount;

    /**
     * Genres associated with the movie.
     */
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    /**
     * Publishes the movie, making it visible to users.
     */
    public void publish() {
        this.published = true;
    }

    /**
     * Unpublishes the movie without deleting it.
     */
    public void unpublish() {
        this.published = false;
    }

    /**
     * Adds a genre to the movie.
     *
     * @param genre the genre to associate with the movie
     */
    public void addGenre(Genre genre) {
        if (genre != null) {
            genres.add(genre);
        }
    }

    /**
     * Removes a genre from the movie.
     *
     * @param genre the genre to remove from the movie
     */
    public void removeGenre(Genre genre) {
        if (genre != null) {
            genres.remove(genre);
        }
    }

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
     * Two movies are considered equal when they share the same UUID.
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
     * Computes the hash code using the immutable public UUID.
     *
     * @return the hash code of the movie
     */
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
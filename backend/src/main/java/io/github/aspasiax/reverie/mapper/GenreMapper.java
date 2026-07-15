package io.github.aspasiax.reverie.mapper;

import io.github.aspasiax.reverie.domain.Genre;
import io.github.aspasiax.reverie.dto.genre.CreateGenreRequest;
import io.github.aspasiax.reverie.dto.genre.GenreResponse;
import io.github.aspasiax.reverie.dto.genre.UpdateGenreRequest;
import org.springframework.stereotype.Component;

/**
 * Maps genre request DTOs to genre entities and genre entities
 * to API response DTOs.
 */
@Component
public class GenreMapper {

    /**
     * Creates a new genre entity from a genre creation request.
     *
     * @param request the genre creation request
     * @return a new genre entity
     */
    public Genre toEntity(CreateGenreRequest request) {
        return Genre.builder()
                .name(request.name().trim())
                .description(trimToNull(request.description()))
                .icon(trimToNull(request.icon()))
                .color(normalizeColor(request.color()))
                .build();
    }

    /**
     * Applies the values of an update request to an existing genre.
     *
     * <p>The genre UUID, database identifier, auditing fields and
     * movie associations are not modified by this method.</p>
     *
     * @param genre   the existing genre entity
     * @param request the genre update request
     */
    public void updateEntity(
            Genre genre,
            UpdateGenreRequest request
    ) {
        genre.setName(request.name().trim());
        genre.setDescription(trimToNull(request.description()));
        genre.setIcon(trimToNull(request.icon()));
        genre.setColor(normalizeColor(request.color()));
    }

    /**
     * Maps a genre entity to the API response DTO.
     *
     * @param genre the genre entity
     * @return the genre response
     */
    public GenreResponse toResponse(Genre genre) {
        return new GenreResponse(
                genre.getUuid(),
                genre.getName(),
                genre.getDescription(),
                genre.getIcon(),
                genre.getColor(),
                genre.getCreatedAt(),
                genre.getUpdatedAt()
        );
    }

    /**
     * Trims a string and converts blank values to {@code null}.
     *
     * @param value the value to normalize
     * @return the trimmed value or {@code null} when blank
     */
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        return trimmedValue.isEmpty() ? null : trimmedValue;
    }

    /**
     * Normalizes an optional hexadecimal color value.
     *
     * <p>The value is trimmed and converted to uppercase to keep
     * color formatting consistent in the database.</p>
     *
     * @param color the hexadecimal color value
     * @return the normalized color or {@code null}
     */
    private String normalizeColor(String color) {
        String normalizedColor = trimToNull(color);

        return normalizedColor == null
                ? null
                : normalizedColor.toUpperCase();
    }
}
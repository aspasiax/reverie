package io.github.aspasiax.reverie.dto.genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents the data required to update an existing movie genre.
 *
 * @param name        the unique genre name
 * @param description the optional genre description
 * @param icon        the optional frontend icon name
 * @param color       the optional frontend badge color
 */
public record UpdateGenreRequest(

        @NotBlank(message = "{validation.genre.name.required}")
        @Size(
                max = 100,
                message = "{validation.genre.name.size}"
        )
        String name,

        @Size(
                max = 500,
                message = "{validation.genre.description.size}"
        )
        String description,

        @Size(
                max = 100,
                message = "{validation.genre.icon.size}"
        )
        String icon,

        @Size(
                max = 20,
                message = "{validation.genre.color.size}"
        )
        @Pattern(
                regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                message = "{validation.genre.color.invalid}"
        )
        String color
) {
}
package io.github.aspasiax.reverie.dto.genre;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(
        name = "UpdateGenreRequest",
        description = "Data required to update an existing movie genre."
)
public record UpdateGenreRequest(

        @Schema(
                description = "Unique genre name.",
                example = "Science Fiction",
                requiredMode = Schema.RequiredMode.REQUIRED,
                maxLength = 100
        )
        @NotBlank(message = "{validation.genre.name.required}")
        @Size(
                max = 100,
                message = "{validation.genre.name.size}"
        )
        String name,

        @Schema(
                description = "Optional description of the genre.",
                example = "Stories focused on futuristic science, technology, space exploration, or alternate realities.",
                maxLength = 500
        )
        @Size(
                max = 500,
                message = "{validation.genre.description.size}"
        )
        String description,

        @Schema(
                description = "Frontend icon name used to represent the genre.",
                example = "rocket",
                maxLength = 100
        )
        @Size(
                max = 100,
                message = "{validation.genre.icon.size}"
        )
        String icon,

        @Schema(
                description = "Hexadecimal color used for the genre badge.",
                example = "#6C63FF",
                pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                maxLength = 20
        )
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
package io.github.aspasiax.reverie.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A single page of results returned by the API.
 *
 * <p>Spring Data's own page implementation is deliberately not exposed.
 * Its serialised form carries nested pagination metadata that clients do
 * not need, and Spring Data documents that its structure is not a stable
 * API contract. This record keeps the response shape explicit and under
 * the application's control.</p>
 *
 * @param content       the results contained in this page
 * @param page          the zero based index of this page
 * @param size          the maximum number of results per page
 * @param totalElements the total number of results across all pages
 * @param totalPages    the total number of pages available
 * @param first         whether this is the first page
 * @param last          whether this is the last page
 * @param <T>           the type of the results
 */
@Schema(
        name = "PageResponse",
        description = "A single page of results together with its pagination metadata."
)
public record PageResponse<T>(

        @Schema(description = "The results contained in this page.")
        List<T> content,

        @Schema(description = "Zero based index of this page.", example = "0")
        int page,

        @Schema(description = "Maximum number of results per page.", example = "20")
        int size,

        @Schema(description = "Total number of results across all pages.", example = "137")
        long totalElements,

        @Schema(description = "Total number of pages available.", example = "7")
        int totalPages,

        @Schema(description = "Whether this is the first page.")
        boolean first,

        @Schema(description = "Whether this is the last page.")
        boolean last
) {

    /**
     * Creates a page response from a Spring Data page.
     *
     * @param page the page produced by the repository layer
     * @param <T>  the type of the results
     * @return the API representation of the page
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
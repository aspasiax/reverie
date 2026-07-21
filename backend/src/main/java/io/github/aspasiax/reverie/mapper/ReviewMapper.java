package io.github.aspasiax.reverie.mapper;

import io.github.aspasiax.reverie.domain.Review;
import io.github.aspasiax.reverie.dto.review.ReviewResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link Review} entities to response DTOs.
 */
@Component
public class ReviewMapper {

    /**
     * Converts a {@link Review} entity to a {@link ReviewResponse}.
     *
     * @param review the review entity
     * @return the mapped response DTO
     */
    public ReviewResponse toResponse(Review review) {

        if (review == null) {
            return null;
        }

        return new ReviewResponse(
                review.getUuid(),
                review.getUser().getUuid(),
                review.getUser().getUsername(),
                review.getMovie().getUuid(),
                review.getMovie().getTitle(),
                review.getMovie().getPosterPath(),
                review.getRating(),
                review.getReviewText(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
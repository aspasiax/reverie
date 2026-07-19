package io.github.aspasiax.reverie.mapper;

import io.github.aspasiax.reverie.domain.WatchLog;
import io.github.aspasiax.reverie.dto.watchlog.WatchLogResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link WatchLog} entities to {@link WatchLogResponse} DTOs.
 *
 * <p>
 * This mapper is responsible for converting watch log entities into
 * response objects returned by the REST API.
 * </p>
 */
@Component
public class WatchLogMapper {

    /**
     * Converts a {@link WatchLog} entity into a {@link WatchLogResponse}.
     *
     * @param watchLog the watch log entity
     * @return the corresponding response DTO
     */
    public WatchLogResponse toResponse(WatchLog watchLog) {

        return new WatchLogResponse(
                watchLog.getUuid(),
                watchLog.getMovie().getUuid(),
                watchLog.getMovie().getTitle(),
                watchLog.getMovie().getPosterPath(),
                watchLog.getWatchedAt(),
                watchLog.getCreatedAt()
        );
    }
}
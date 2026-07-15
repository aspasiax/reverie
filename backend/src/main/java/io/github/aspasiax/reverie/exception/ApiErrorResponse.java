package io.github.aspasiax.reverie.exception;

import java.time.Instant;

/**
 * Represents the standard error response returned by the API.
 *
 * @param timestamp the time the error occurred
 * @param status    the HTTP status code
 * @param error     the HTTP status reason phrase
 * @param message   the error message
 * @param path      the request path
 */
public record ApiErrorResponse(

        Instant timestamp,

        int status,

        String error,

        String message,

        String path
) {
}
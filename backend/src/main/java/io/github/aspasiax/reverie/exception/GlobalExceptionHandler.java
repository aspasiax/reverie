package io.github.aspasiax.reverie.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Handles application exceptions globally and converts them into
 * consistent API error responses.
 *
 * <p>Each handled exception is mapped to an appropriate HTTP status
 * and returned using the standard {@link ApiErrorResponse} format.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests for movies that do not exist or have been
     * soft deleted.
     *
     * @param exception the movie-not-found exception
     * @param request   the current HTTP request
     * @return a {@code 404 Not Found} response
     */
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMovieNotFound(
            MovieNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles requests involving genres that do not exist or have
     * been soft deleted.
     *
     * @param exception the genre-not-found exception
     * @param request   the current HTTP request
     * @return a {@code 404 Not Found} response
     */
    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleGenreNotFound(
            GenreNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles duplicate external movie identifiers.
     *
     * @param exception the duplicate identifier exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(DuplicateMovieIdentifierException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateMovieIdentifier(
            DuplicateMovieIdentifierException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles invalid arguments that violate application business rules.
     *
     * <p>This handler is currently also used by existing authentication
     * checks until more specific authentication exceptions are added.</p>
     *
     * @param exception the illegal-argument exception
     * @param request   the current HTTP request
     * @return a {@code 400 Bad Request} response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles validation failures produced by request DTO constraints.
     *
     * <p>All field validation messages are combined into a single,
     * readable response message.</p>
     *
     * @param exception the request validation exception
     * @param request   the current HTTP request
     * @return a {@code 400 Bad Request} response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .distinct()
                .collect(Collectors.joining("; "));

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    /**
     * Handles unexpected exceptions that are not covered by a more
     * specific exception handler.
     *
     * <p>Internal implementation details are not exposed to the client.</p>
     *
     * @param exception the unexpected exception
     * @param request   the current HTTP request
     * @return a {@code 500 Internal Server Error} response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                request.getRequestURI()
        );
    }

    /**
     * Creates the standard API error response.
     *
     * @param status  the HTTP response status
     * @param message the error message
     * @param path    the request path
     * @return the completed response entity
     */
    private ResponseEntity<ApiErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            String path
    ) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

    /**
     * Formats a validation field error into a readable message.
     *
     * @param fieldError the validation field error
     * @return the formatted validation message
     */
    private String formatFieldError(FieldError fieldError) {
        String message = fieldError.getDefaultMessage();

        if (message == null || message.isBlank()) {
            message = "Invalid value";
        }

        return fieldError.getField() + ": " + message;
    }
}
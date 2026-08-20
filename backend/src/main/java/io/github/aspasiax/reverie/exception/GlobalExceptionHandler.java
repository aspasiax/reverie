package io.github.aspasiax.reverie.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Handles application exceptions globally and converts them into
 * consistent API error responses.
 *
 * <p>Each handled exception is mapped to an appropriate HTTP status
 * and returned using the standard {@link ApiErrorResponse} format.</p>
 *
 * <p>The handlers are grouped by the status they return, and within each
 * group the exceptions of this application come before those of the
 * framework, which act as the fallbacks. The order is for the eye only:
 * Spring selects a handler by how closely its type matches the thrown
 * exception, never by where it appears in this file.</p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 400 Bad Request

    /**
     * Handles attempts to create reviews without an active watch log.
     *
     * @param exception the watch log required exception
     * @param request   the current HTTP request
     * @return a {@code 400 Bad Request} response
     */
    @ExceptionHandler(WatchLogRequiredException.class)
    public ResponseEntity<ApiErrorResponse> handleWatchLogRequired(
            WatchLogRequiredException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles reviews that do not contain a rating or review text.
     *
     * @param exception the invalid review exception
     * @param request   the current HTTP request
     * @return a {@code 400 Bad Request} response
     */
    @ExceptionHandler(InvalidReviewException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidReview(
            InvalidReviewException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles password changes submitted with the wrong current password.
     *
     * @param exception the invalid current password exception
     * @param request   the current HTTP request
     * @return a {@code 400 Bad Request} response
     */
    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCurrentPassword(
            InvalidCurrentPasswordException exception,
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
     * Handles request bodies that cannot be parsed, such as malformed
     * JSON or values that do not match the expected field types.
     *
     * @param exception the message conversion failure
     * @param request   the current HTTP request
     * @return a {@code 400 Bad Request} response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "The request body is missing or malformed.",
                request.getRequestURI()
        );
    }

    /**
     * Handles path variables and request parameters that cannot be
     * converted to the expected type, such as an invalid UUID.
     *
     * @param exception the type conversion failure
     * @param request   the current HTTP request
     * @return a {@code 400 Bad Request} response
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter '" + exception.getName() + "'.",
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

    // 401 Unauthorized

    /**
     * Handles operations that require an authenticated user when no
     * authenticated user is present in the security context.
     *
     * @param exception the authentication required exception
     * @param request   the current HTTP request
     * @return a {@code 401 Unauthorized} response
     */
    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationRequired(
            AuthenticationRequiredException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles authentication failures caused by invalid credentials.
     *
     * @param exception the bad credentials exception
     * @param request   the current HTTP request
     * @return a {@code 401 Unauthorized} response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password.",
                request.getRequestURI()
        );
    }

    /**
     * Handles sign in attempts from an account that has been disabled.
     *
     * <p>The credentials were correct, so reporting them as invalid would
     * send the owner hunting for a password problem that does not exist.
     * The state of the account is named instead.</p>
     *
     * @param exception the disabled account exception
     * @param request   the current HTTP request
     * @return a {@code 401 Unauthorized} response
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiErrorResponse> handleDisabledAccount(
            DisabledException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "This account has been disabled.",
                request.getRequestURI()
        );
    }

    // 403 Forbidden

    /**
     * Handles attempts to modify reviews owned by another user.
     *
     * @param exception the review access denied exception
     * @param request   the current HTTP request
     * @return a {@code 403 Forbidden} response
     */
    @ExceptionHandler(ReviewAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleReviewAccessDenied(
            ReviewAccessDeniedException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles authorization failures raised by the capability checks
     * declared on the controllers.
     *
     * <p>Method security throws this exception inside the controller
     * invocation, so it reaches this advice rather than the access denied
     * handler configured on the security filter chain.</p>
     *
     * @param exception the authorization failure
     * @param request   the current HTTP request
     * @return a {@code 403 Forbidden} response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "You do not have permission to perform this operation.",
                request.getRequestURI()
        );
    }

    // 404 Not Found

    /**
     * Handles requests for users that do not exist or have been
     * soft deleted.
     *
     * @param exception the user-not-found exception
     * @param request   the current HTTP request
     * @return a {@code 404 Not Found} response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles requests referring to a security role that does not exist.
     *
     * @param exception the role-not-found exception
     * @param request   the current HTTP request
     * @return a {@code 404 Not Found} response
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleNotFound(
            RoleNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

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
     * Handles requests for watch logs that do not exist or have
     * been soft deleted.
     *
     * @param exception the watch log not found exception
     * @param request   the current HTTP request
     * @return a {@code 404 Not Found} response
     */
    @ExceptionHandler(WatchLogNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleWatchLogNotFound(
            WatchLogNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles requests for reviews that do not exist or have
     * been soft deleted.
     *
     * @param exception the review not found exception
     * @param request   the current HTTP request
     * @return a {@code 404 Not Found} response
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleReviewNotFound(
            ReviewNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles requests for watchlist entries that do not exist or have
     * been removed.
     *
     * @param exception the watchlist entry not found exception
     * @param request   the current HTTP request
     * @return a {@code 404 Not Found} response
     */
    @ExceptionHandler(WatchlistEntryNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleWatchlistEntryNotFound(
            WatchlistEntryNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles requests addressed to endpoints that do not exist.
     *
     * @param exception the missing resource exception
     * @param request   the current HTTP request
     * @return a {@code 404 Not Found} response
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "The requested endpoint does not exist.",
                request.getRequestURI()
        );
    }


    // 405 Method Not Allowed

    /**
     * Handles requests that reach a known address with the wrong verb.
     *
     * <p>Without this the request falls through to the handler of last
     * resort and is reported as a fault of the server, which it is not:
     * nothing failed, the caller asked for something the address does not
     * offer. The distinction matters to anyone reading the API through
     * Swagger, where the verb is the first thing they get wrong.</p>
     *
     * @param exception the unsupported method exception
     * @param request   the current HTTP request
     * @return a {@code 405 Method Not Allowed} response
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    // 409 Conflict

    /**
     * Handles registrations that reuse an existing username or email address.
     *
     * @param exception the duplicate credential exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(DuplicateCredentialException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateCredential(
            DuplicateCredentialException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles attempts by an administrator to change their own role.
     *
     * @param exception the self role change exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(SelfRoleChangeException.class)
    public ResponseEntity<ApiErrorResponse> handleSelfRoleChange(
            SelfRoleChangeException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles an administrator attempting to disable their own account.
     *
     * @param exception the thrown exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(SelfDisableException.class)
    public ResponseEntity<ApiErrorResponse> handleSelfDisable(
            SelfDisableException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
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
     * Handles duplicate genre names.
     *
     * @param exception the duplicate genre name exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(DuplicateGenreNameException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateGenreName(
            DuplicateGenreNameException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles attempts to create duplicate reviews.
     *
     * @param exception the duplicate review exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateReview(
            DuplicateReviewException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles films added to a watchlist they are already on.
     *
     * @param exception the duplicate entry exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(DuplicateWatchlistEntryException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateWatchlistEntry(
            DuplicateWatchlistEntryException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles restore requests for records that are not deleted.
     *
     * @param exception the restore-not-applicable exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(RestoreNotApplicableException.class)
    public ResponseEntity<ApiErrorResponse> handleRestoreNotApplicable(
            RestoreNotApplicableException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles permanent deletions requested for records that are still active.
     *
     * @param exception the thrown exception
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(PermanentDeleteNotApplicableException.class)
    public ResponseEntity<ApiErrorResponse> handlePermanentDeleteNotApplicable(
            PermanentDeleteNotApplicableException exception,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Handles database constraint violations that survive the
     * application-level validation checks.
     *
     * <p>Uniqueness is verified before writing, but concurrent requests
     * may still reach the database at the same time. The resulting
     * constraint violation is reported as a conflict rather than as an
     * unexpected server error.</p>
     *
     * @param exception the constraint violation
     * @param request   the current HTTP request
     * @return a {@code 409 Conflict} response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Database constraint violated for request {}",
                request.getRequestURI(),
                exception
        );

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "The operation conflicts with existing data.",
                request.getRequestURI()
        );
    }

    // 500 Internal Server Error

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
        log.error(
                "Unhandled exception for request {}",
                request.getRequestURI(),
                exception
        );

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                request.getRequestURI()
        );
    }

    // Shared response building

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
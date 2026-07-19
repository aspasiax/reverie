package io.github.aspasiax.reverie.exception;

/**
 * Thrown when an operation requires an authenticated user,
 * but no authenticated user is available.
 */
public class AuthenticationRequiredException extends RuntimeException {

    /**
     * Creates a new authentication required exception.
     */
    public AuthenticationRequiredException() {
        super("Authentication is required to perform this operation.");
    }

}
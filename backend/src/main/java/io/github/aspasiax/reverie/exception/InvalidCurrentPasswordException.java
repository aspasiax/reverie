package io.github.aspasiax.reverie.exception;

/**
 * Thrown when a password change is attempted with the wrong current password.
 *
 * <p>Deliberately not an authentication failure. The caller is signed in and
 * stays signed in: they mistyped a field, and taking their session away over
 * a typo would be a strange way to help.</p>
 */
public class InvalidCurrentPasswordException extends RuntimeException {

    /**
     * Creates a new invalid current password exception.
     */
    public InvalidCurrentPasswordException() {
        super("The current password is not correct.");
    }
}
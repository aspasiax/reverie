package io.github.aspasiax.reverie.exception;

/**
 * Thrown when an administrator attempts to disable their own account.
 *
 * <p>Allowing this would let an administrator lock themselves out of the
 * application, with no way back in through the API.</p>
 */
public class SelfDisableException extends RuntimeException {

    /**
     * Creates a self disable exception.
     */
    public SelfDisableException() {
        super("You cannot disable your own account.");
    }
}
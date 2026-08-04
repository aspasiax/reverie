package io.github.aspasiax.reverie.exception;

/**
 * Thrown when an administrator attempts to change their own security role.
 *
 * <p>Allowing this would let the last administrator remove their own
 * privileges, leaving no account able to grant them again.</p>
 */
public class SelfRoleChangeException extends RuntimeException {

    /**
     * Creates a self role change exception.
     */
    public SelfRoleChangeException() {
        super("You cannot change your own role.");
    }
}
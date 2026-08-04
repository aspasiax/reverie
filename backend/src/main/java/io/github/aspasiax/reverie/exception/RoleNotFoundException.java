package io.github.aspasiax.reverie.exception;

/**
 * Thrown when a security role cannot be found by name.
 */
public class RoleNotFoundException extends RuntimeException {

    /**
     * Creates a role-not-found exception for the given name.
     *
     * @param name the requested role name
     */
    public RoleNotFoundException(String name) {
        super("Role '" + name + "' was not found.");
    }
}
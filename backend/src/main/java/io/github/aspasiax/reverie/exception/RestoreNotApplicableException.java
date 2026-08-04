package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Thrown when a restore is requested for a record that is not deleted.
 *
 * <p>The request conflicts with the current state of the resource rather
 * than being malformed or forbidden, so it is reported as a conflict.</p>
 */
public class RestoreNotApplicableException extends RuntimeException {

    /**
     * Creates a restore-not-applicable exception.
     *
     * @param resourceType the type of resource, for example {@code Movie}
     * @param uuid         the public identifier of the resource
     */
    public RestoreNotApplicableException(String resourceType, UUID uuid) {
        super(resourceType + " with UUID " + uuid + " is not deleted.");
    }
}
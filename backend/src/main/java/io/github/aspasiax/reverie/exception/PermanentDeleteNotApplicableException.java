package io.github.aspasiax.reverie.exception;

import java.util.UUID;

/**
 * Thrown when a record is permanently deleted before it has been soft deleted.
 *
 * <p>Destroying a record is irreversible, so it is only allowed once the
 * record has already been withdrawn from use. Requesting it for an active
 * record conflicts with the current state of the resource rather than being
 * malformed or forbidden, so it is reported as a conflict.</p>
 */
public class PermanentDeleteNotApplicableException extends RuntimeException {

    /**
     * Creates a permanent-delete-not-applicable exception.
     *
     * @param resourceType the type of resource, for example {@code Genre}
     * @param uuid         the public identifier of the resource
     */
    public PermanentDeleteNotApplicableException(String resourceType, UUID uuid) {
        super(resourceType + " with UUID " + uuid + " must be deleted before it can be destroyed.");
    }
}
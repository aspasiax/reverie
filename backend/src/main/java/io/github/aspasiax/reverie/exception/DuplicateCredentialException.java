package io.github.aspasiax.reverie.exception;

/**
 * Thrown when a registration uses a username or an email address that is
 * already taken by an active account.
 *
 * <p>The value is reported back so that the person registering knows which
 * of the two fields to change.</p>
 */
public class DuplicateCredentialException extends RuntimeException {

    /**
     * Creates a duplicate credential exception.
     *
     * @param credentialType the field in question, for example {@code Username}
     * @param value          the value that is already taken
     */
    public DuplicateCredentialException(String credentialType, String value) {
        super(credentialType + " '" + value + "' is already in use.");
    }
}
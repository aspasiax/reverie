package io.github.aspasiax.reverie.exception;

/**
 * Thrown when a review does not contain any meaningful content.
 *
 * <p>A review must contain at least one of the following:</p>
 *
 * <ul>
 *     <li>a numerical rating, or</li>
 *     <li>non-blank review text.</li>
 * </ul>
 *
 * <p>This exception protects the application from creating or updating
 * reviews that contain neither a rating nor written content.</p>
 */
public class InvalidReviewException extends RuntimeException {

    /**
     * Creates a new invalid review exception with the standard
     * business-rule violation message.
     */
    public InvalidReviewException() {
        super("A review must contain either a rating or review text.");
    }
}
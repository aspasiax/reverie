package io.github.aspasiax.reverie.exception;

/**
 * Thrown when an external movie identifier is already
 * assigned to another movie.
 *
 * <p>This exception is used for unique identifiers such
 * as TMDB and IMDb ids.</p>
 */
public class DuplicateMovieIdentifierException
        extends RuntimeException {

    /**
     * Creates a duplicate movie identifier exception.
     *
     * @param identifierType the identifier type
     *                       (for example TMDB or IMDb)
     * @param identifierValue the duplicate identifier value
     */
    public DuplicateMovieIdentifierException(
            String identifierType,
            Object identifierValue
    ) {
        super(identifierType + " identifier '" +
                identifierValue +
                "' is already in use.");
    }
}
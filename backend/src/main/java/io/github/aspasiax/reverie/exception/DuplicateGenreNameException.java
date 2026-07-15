package io.github.aspasiax.reverie.exception;

/**
 * Thrown when a genre name is already assigned to another genre.
 */
public class DuplicateGenreNameException extends RuntimeException {

    /**
     * Creates a duplicate genre name exception.
     *
     * @param name the duplicate genre name
     */
    public DuplicateGenreNameException(String name) {
        super("Genre name '" + name + "' is already in use.");
    }
}
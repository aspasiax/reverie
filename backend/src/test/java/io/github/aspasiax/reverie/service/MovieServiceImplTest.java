package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.dto.movie.UpdateMovieRequest;
import io.github.aspasiax.reverie.exception.DuplicateMovieIdentifierException;
import io.github.aspasiax.reverie.exception.RestoreNotApplicableException;
import io.github.aspasiax.reverie.mapper.MovieMapper;
import io.github.aspasiax.reverie.repository.GenreRepository;
import io.github.aspasiax.reverie.repository.MovieRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks the two rules that govern changing a film.
 *
 * <p>A film may not be restored unless it was deleted, and no two films may
 * claim the same identifier at TMDB or IMDb. The second rule has to ignore
 * the film being edited: the check looks the identifier up and will always
 * find the very film that owns it, so without comparing identities no film
 * could ever be saved twice.</p>
 */
@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private final UUID movieUuid = UUID.randomUUID();

    private static final long tmdbId = 27205L;

    /** A film in the catalogue, identified by its database id. */
    private Movie film(long id, UUID uuid) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setUuid(uuid);
        movie.setTitle("Inception");
        return movie;
    }

    /** An edit that leaves the film carrying the given TMDB identifier. */
    private UpdateMovieRequest editWith(Long tmdb) {
        return new UpdateMovieRequest(
                "Inception", null, null, null, null, null, null, null,
                tmdb, null, true, null
        );
    }

    @Test
    @DisplayName("restoring a film that was never deleted is refused")
    void restoreRefusesAFilmThatIsNotDeleted() {
        Movie movie = film(1L, movieUuid);

        when(movieRepository.findByUuid(movieUuid)).thenReturn(Optional.of(movie));

        assertThatThrownBy(() -> movieService.restore(movieUuid))
                .isInstanceOf(RestoreNotApplicableException.class);

        verify(movieRepository, never()).save(any());
    }

    @Test
    @DisplayName("a film keeps its own external identifier when edited")
    void updateAllowsAFilmToKeepItsOwnIdentifier() {
        Movie movie = film(1L, movieUuid);

        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.of(movie));
        when(movieRepository.findByTmdbIdAndDeletedFalse(tmdbId))
                .thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);

        movieService.update(movieUuid, editWith(tmdbId));

        /*
         * The identifier was found, and it belongs to this film. Treating
         * that as a collision would make every film uneditable, which is
         * the failure this test exists to catch.
         */
        verify(movieRepository).save(movie);
    }

    @Test
    @DisplayName("an external identifier held by another film is refused")
    void updateRefusesAnIdentifierHeldByAnotherFilm() {
        Movie movie = film(1L, movieUuid);
        Movie other = film(2L, UUID.randomUUID());

        when(movieRepository.findByUuidAndDeletedFalse(movieUuid))
                .thenReturn(Optional.of(movie));
        when(movieRepository.findByTmdbIdAndDeletedFalse(tmdbId))
                .thenReturn(Optional.of(other));

        assertThatThrownBy(() -> movieService.update(movieUuid, editWith(tmdbId)))
                .isInstanceOf(DuplicateMovieIdentifierException.class);

        verify(movieRepository, never()).save(any());
    }
}
package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Genre;
import io.github.aspasiax.reverie.dto.genre.GenreResponse;
import io.github.aspasiax.reverie.exception.GenreNotFoundException;
import io.github.aspasiax.reverie.exception.RestoreNotApplicableException;
import io.github.aspasiax.reverie.mapper.GenreMapper;
import io.github.aspasiax.reverie.repository.GenreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks the rules the genre service enforces on its own.
 *
 * <p>The repository and the mapper are replaced by stand-ins, so these
 * tests need no database and no Spring context. What is being examined is
 * the decision the service makes, not the plumbing around it.</p>
 */
@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    @DisplayName("restoring a genre that was never deleted is refused")
    void restoreRefusesAGenreThatIsNotDeleted() {
        UUID uuid = UUID.randomUUID();

        Genre active = new Genre();
        active.setName("Horror");

        when(genreRepository.findByUuid(uuid)).thenReturn(Optional.of(active));

        assertThatThrownBy(() -> genreService.restore(uuid))
                .isInstanceOf(RestoreNotApplicableException.class);

        /*
         * The interesting part is not only that it complained, but that it
         * changed nothing on the way out.
         */
        verify(genreRepository, never()).save(any());
    }

    @Test
    @DisplayName("restoring a genre that does not exist is refused")
    void restoreRefusesAGenreThatDoesNotExist() {
        UUID uuid = UUID.randomUUID();

        when(genreRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.restore(uuid))
                .isInstanceOf(GenreNotFoundException.class);

        verify(genreRepository, never()).save(any());
    }

    @Test
    @DisplayName("a deleted genre comes back when its name is free")
    void restoreBringsBackADeletedGenre() {
        UUID uuid = UUID.randomUUID();

        Genre deleted = new Genre();
        deleted.setId(7L);
        deleted.setName("Horror");
        deleted.softDelete();

        when(genreRepository.findByUuid(uuid)).thenReturn(Optional.of(deleted));
        when(genreRepository.findByNameIgnoreCaseAndDeletedFalse("Horror"))
                .thenReturn(Optional.empty());
        when(genreRepository.save(deleted)).thenReturn(deleted);
        when(genreMapper.toResponse(deleted)).thenReturn(
                new GenreResponse(uuid, "Horror", null, null, null, Instant.now(), Instant.now())
        );

        GenreResponse response = genreService.restore(uuid);

        assertThat(response.name()).isEqualTo("Horror");
        assertThat(deleted.isDeleted()).isFalse();

        verify(genreRepository).save(deleted);
    }
}
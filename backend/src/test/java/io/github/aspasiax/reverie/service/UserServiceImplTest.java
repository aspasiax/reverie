package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Movie;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.user.ChangePasswordRequest;
import io.github.aspasiax.reverie.dto.user.UpdateUserRequest;
import io.github.aspasiax.reverie.dto.user.UpdateUserRoleRequest;
import io.github.aspasiax.reverie.exception.InvalidCurrentPasswordException;
import io.github.aspasiax.reverie.exception.SelfDisableException;
import io.github.aspasiax.reverie.exception.SelfRoleChangeException;
import io.github.aspasiax.reverie.exception.WatchLogRequiredException;
import io.github.aspasiax.reverie.mapper.UserMapper;
import io.github.aspasiax.reverie.repository.MovieRepository;
import io.github.aspasiax.reverie.repository.RoleRepository;
import io.github.aspasiax.reverie.repository.UserRepository;
import io.github.aspasiax.reverie.repository.WatchLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks the guards that stop an administrator locking themselves out, the
 * one that protects a password change, and the rule behind a favourite film.
 *
 * <p>The first three exist because the alternative is unrecoverable through
 * the API: an administrator who demotes or disables their own account has no
 * way back in. The last exists because a favourite is a claim about a film
 * you have seen, and nothing in the database can check that.</p>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private WatchLogRepository watchLogRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ICurrentUserService currentUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private final UUID myUuid = UUID.randomUUID();

    private final UUID filmUuid = UUID.randomUUID();

    /** The account making the request. */
    private User me() {
        User user = new User();
        user.setUuid(myUuid);
        user.setPassword("hashed-current-password");
        return user;
    }

    /** The film being named as a favourite. */
    private Movie film() {
        Movie movie = new Movie();
        movie.setUuid(filmUuid);
        movie.setTitle("Interstellar");
        return movie;
    }

    /** A profile update naming the given film as a favourite. */
    private UpdateUserRequest requestNaming(UUID movieUuid) {
        return new UpdateUserRequest("Alex", null, null, movieUuid);
    }

    @Test
    @DisplayName("an administrator cannot change their own role")
    void updateRoleRefusesTheCallersOwnAccount() {
        User me = me();

        when(currentUserService.getCurrentUser()).thenReturn(me);
        when(userRepository.findByUuidAndDeletedFalse(myUuid)).thenReturn(Optional.of(me));

        UpdateUserRoleRequest request = new UpdateUserRoleRequest("USER");

        assertThatThrownBy(() -> userService.updateUserRole(myUuid, request))
                .isInstanceOf(SelfRoleChangeException.class);

        /*
         * The role is never looked up: the guard runs before anything that
         * could partly apply the change.
         */
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("an administrator cannot disable their own account")
    void disableRefusesTheCallersOwnAccount() {
        User me = me();

        when(currentUserService.getCurrentUser()).thenReturn(me);
        when(userRepository.findByUuidAndDeletedFalse(myUuid)).thenReturn(Optional.of(me));

        assertThatThrownBy(() -> userService.disable(myUuid))
                .isInstanceOf(SelfDisableException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("a password change with the wrong current password changes nothing")
    void changePasswordRefusesAWrongCurrentPassword() {
        User me = me();

        when(currentUserService.getCurrentUser()).thenReturn(me);
        when(passwordEncoder.matches("guessed", "hashed-current-password"))
                .thenReturn(false);

        ChangePasswordRequest request =
                new ChangePasswordRequest("guessed", "Newpass123!");

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(InvalidCurrentPasswordException.class);

        /*
         * Nothing is encoded and nothing is stored. A failed attempt has to
         * leave the account exactly as it was.
         */
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("a favourite film the user has not watched is refused")
    void updateProfileRefusesAnUnwatchedFavourite() {
        User me = me();

        when(currentUserService.getCurrentUser()).thenReturn(me);
        when(movieRepository.findByUuidAndDeletedFalse(filmUuid))
                .thenReturn(Optional.of(film()));
        when(watchLogRepository.existsByUserUuidAndMovieUuidAndDeletedFalse(myUuid, filmUuid))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.updateCurrentUserProfile(requestNaming(filmUuid)))
                .isInstanceOf(WatchLogRequiredException.class);

        /*
         * The rest of the profile is refused along with the favourite. The
         * whole update is one request, so a rejected part rejects all of it
         * rather than saving half a form.
         */
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("a favourite film the user has watched is stored")
    void updateProfileAcceptsAWatchedFavourite() {
        User me = me();
        Movie film = film();

        when(currentUserService.getCurrentUser()).thenReturn(me);
        when(movieRepository.findByUuidAndDeletedFalse(filmUuid))
                .thenReturn(Optional.of(film));
        when(watchLogRepository.existsByUserUuidAndMovieUuidAndDeletedFalse(myUuid, filmUuid))
                .thenReturn(true);
        when(userRepository.save(me)).thenReturn(me);

        userService.updateCurrentUserProfile(requestNaming(filmUuid));

        assertThat(me.getFavouriteMovie()).isSameAs(film);
    }

    @Test
    @DisplayName("naming no favourite film looks nothing up")
    void updateProfileClearsTheFavouriteWithoutAskingTheCatalogue() {
        User me = me();
        me.setFavouriteMovie(film());

        when(currentUserService.getCurrentUser()).thenReturn(me);
        when(userRepository.save(me)).thenReturn(me);

        userService.updateCurrentUserProfile(requestNaming(null));

        assertThat(me.getFavouriteMovie()).isNull();

        /*
         * Clearing a choice is not a choice to be checked. Reaching the
         * catalogue or the history here would mean the absent case travels
         * the same path as the present one, which is how a null ends up
         * being looked up.
         */
        verifyNoInteractions(movieRepository, watchLogRepository);
    }
}
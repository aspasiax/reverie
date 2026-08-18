package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.user.ChangePasswordRequest;
import io.github.aspasiax.reverie.dto.user.UpdateUserRoleRequest;
import io.github.aspasiax.reverie.exception.InvalidCurrentPasswordException;
import io.github.aspasiax.reverie.exception.SelfDisableException;
import io.github.aspasiax.reverie.exception.SelfRoleChangeException;
import io.github.aspasiax.reverie.mapper.UserMapper;
import io.github.aspasiax.reverie.repository.RoleRepository;
import io.github.aspasiax.reverie.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks the guards that stop an administrator locking themselves out, and
 * the one that protects a password change.
 *
 * <p>Each exists because the alternative is unrecoverable through the API:
 * an administrator who demotes or disables their own account has no way
 * back in.</p>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ICurrentUserService currentUserService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private final UUID myUuid = UUID.randomUUID();

    /** The account making the request. */
    private User me() {
        User user = new User();
        user.setUuid(myUuid);
        user.setPassword("hashed-current-password");
        return user;
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
}
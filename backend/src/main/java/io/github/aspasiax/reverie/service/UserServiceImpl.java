package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Role;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.user.*;
import io.github.aspasiax.reverie.exception.*;
import io.github.aspasiax.reverie.mapper.UserMapper;
import io.github.aspasiax.reverie.repository.RoleRepository;
import io.github.aspasiax.reverie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Default implementation of {@link IUserService}.
 *
 * <p>Handles profile retrieval and updates for the authenticated user, as
 * well as the administrative operations that expose and modify accounts.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final ICurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = currentUserService.getCurrentUser();

        return userMapper.toProfileResponse(currentUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserProfileResponse updateCurrentUserProfile(UpdateUserRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        userMapper.updateEntity(currentUser, request);

        User updatedUser = userRepository.save(currentUser);

        return userMapper.toProfileResponse(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        /*
         * Compared through the encoder rather than directly: what is stored
         * is a hash, and the same password hashes differently every time.
         */
        if (!passwordEncoder.matches(request.currentPassword(), currentUser.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        currentUser.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(currentUser);

        log.info("Password changed for account {}", currentUser.getUuid());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserSummaryResponse findByUuid(UUID uuid) {
        User user = findActiveUser(uuid);

        return userMapper.toSummaryResponse(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserAdminResponse> findAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "displayName"))
                .stream()
                .map(userMapper::toAdminResponse)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserAdminResponse updateUserRole(
            UUID uuid,
            UpdateUserRoleRequest request
    ) {
        User currentUser = currentUserService.getCurrentUser();
        User targetUser = findActiveUser(uuid);

        /*
         * An administrator who demotes their own account could remove the
         * last set of administrative privileges in the system, leaving no
         * way to grant them again through the API.
         */
        if (targetUser.getUuid().equals(currentUser.getUuid())) {
            throw new SelfRoleChangeException();
        }

        String normalizedRoleName = request.roleName()
                .trim()
                .toUpperCase(Locale.ROOT);

        Role role = roleRepository.findByName(normalizedRoleName)
                .orElseThrow(() -> new RoleNotFoundException(normalizedRoleName));

        targetUser.setRole(role);

        User updatedUser = userRepository.save(targetUser);

        log.info(
                "Role of user {} changed to {} by {}",
                targetUser.getUuid(),
                normalizedRoleName,
                currentUser.getUuid()
        );

        return userMapper.toAdminResponse(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserAdminResponse enable(UUID uuid) {
        User targetUser = findActiveUser(uuid);

        targetUser.enable();

        User updatedUser = userRepository.save(targetUser);

        log.info("Account {} enabled", targetUser.getUuid());

        return userMapper.toAdminResponse(updatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserAdminResponse disable(UUID uuid) {
        User currentUser = currentUserService.getCurrentUser();
        User targetUser = findActiveUser(uuid);

        /*
         * An administrator who disables their own account would lose the
         * ability to sign in, and with it the only way to undo the change.
         */
        if (targetUser.getUuid().equals(currentUser.getUuid())) {
            throw new SelfDisableException();
        }

        targetUser.disable();

        User updatedUser = userRepository.save(targetUser);

        log.info(
                "Account {} disabled by {}",
                targetUser.getUuid(),
                currentUser.getUuid()
        );

        return userMapper.toAdminResponse(updatedUser);
    }

    /**
     * Finds a user that has not been soft deleted.
     *
     * @param uuid the public user identifier
     * @return the active user entity
     * @throws UserNotFoundException if no active user exists
     */
    private User findActiveUser(UUID uuid) {
        return userRepository.findByUuidAndDeletedFalse(uuid)
                .orElseThrow(() -> new UserNotFoundException(uuid));
    }
}
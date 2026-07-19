package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.exception.AuthenticationRequiredException;
import io.github.aspasiax.reverie.exception.UserNotFoundException;
import io.github.aspasiax.reverie.repository.UserRepository;
import io.github.aspasiax.reverie.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ICurrentUserService}.
 *
 * <p>
 * Retrieves the authenticated user from Spring Security's
 * {@link SecurityContextHolder} and loads the corresponding
 * {@link User} entity from the database.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements ICurrentUserService {

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            throw new AuthenticationRequiredException();
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return userRepository.findByUuidAndDeletedFalse(userDetails.getUuid())
                .orElseThrow(() ->
                        new UserNotFoundException(userDetails.getUuid()));
    }

}
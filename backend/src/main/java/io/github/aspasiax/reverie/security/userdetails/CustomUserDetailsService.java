package io.github.aspasiax.reverie.security.userdetails;

import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads Reverie users for Spring Security authentication.
 *
 * <p>Although Spring Security names the lookup method
 * {@code loadUserByUsername}, Reverie uses the user's email address
 * as the authentication identifier.</p>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user using their email address and adapts the entity
     * to Spring Security's {@link UserDetails} contract.
     *
     * <p>The transaction remains active while the user's role and
     * capabilities are mapped by {@link CustomUserDetails}.</p>
     *
     * @param email the email supplied during authentication
     * @return the authenticated user's security details
     * @throws UsernameNotFoundException if no user exists with the email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User with email " + email + " was not found."
                ));

        return new CustomUserDetails(user);
    }
}
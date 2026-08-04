package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Role;
import io.github.aspasiax.reverie.domain.User;
import io.github.aspasiax.reverie.dto.auth.AuthResponse;
import io.github.aspasiax.reverie.dto.auth.LoginRequest;
import io.github.aspasiax.reverie.dto.auth.RegisterRequest;
import io.github.aspasiax.reverie.repository.RoleRepository;
import io.github.aspasiax.reverie.repository.UserRepository;
import io.github.aspasiax.reverie.security.jwt.JwtService;
import io.github.aspasiax.reverie.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Default implementation of {@link IAuthenticationService}.
 *
 * <p>Handles user registration and login by coordinating
 * repositories, password encoding, authentication and JWT creation.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Normalize user-provided identity fields before validation and storage.
        String normalizedUsername = request.username()
                .trim()
                .toLowerCase(Locale.ROOT);

        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        String normalizedDisplayName = request.displayName().trim();

        // Validate that the username and email address are available.
        if (userRepository.existsByUsernameIgnoreCaseAndDeletedFalse(normalizedUsername)) {
            throw new IllegalArgumentException(
                    "Username is already in use."
            );
        }

        if (userRepository.existsByEmailIgnoreCaseAndDeletedFalse(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Email is already in use."
            );
        }

        // Retrieve the default role assigned to newly registered users.
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException(
                        "Default role USER was not found."
                ));

        // Create and initialize the new user.
        User user = User.builder()
                .username(normalizedUsername)
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .displayName(normalizedDisplayName)
                .enabled(true)
                .role(userRole)
                .build();

        // Persist the user in the database.
        User savedUser = userRepository.save(user);

        // Generate a JWT access token for the newly registered user.
        CustomUserDetails userDetails =
                new CustomUserDetails(savedUser);

        String token = jwtService.generateToken(userDetails);

        // Return the authentication response.
        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse login(LoginRequest request) {

        // Normalize the email address before authentication.
        String normalizedEmail = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Authenticate the user using the provided credentials.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.password()
                )
        );

        // Retrieve the authenticated user details.
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        // Generate a JWT access token.
        String token = jwtService.generateToken(userDetails);

        // Return the authentication response.
        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000
        );
    }
}
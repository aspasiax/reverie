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

/**
 * Default implementation of {@link IAuthenticationService}.
 *
 * <p>Handles user registration and login by coordinating
 * repositories, password encoding, authentication and JWT creation.</p>
 *
 * @author Aspasia
 * @version 1.0
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
    public AuthResponse register(RegisterRequest request) {

        // Validate the registration request.
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                    "Username is already in use."
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email is already in use."
            );
        }

        // Retrieve the default role assigned to new users.
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException(
                        "Default role USER was not found."
                ));

        // Create and initialize the new user.
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
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

        // Authenticate the user using the provided credentials.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
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
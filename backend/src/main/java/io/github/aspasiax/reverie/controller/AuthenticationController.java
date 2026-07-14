package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.auth.AuthResponse;
import io.github.aspasiax.reverie.dto.auth.LoginRequest;
import io.github.aspasiax.reverie.dto.auth.RegisterRequest;
import io.github.aspasiax.reverie.service.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes authentication endpoints for user registration
 * and login.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    /**
     * Registers a new user account.
     *
     * @param request the registration request
     * @return the generated authentication response
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        AuthResponse response =
                authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Authenticates an existing user.
     *
     * @param request the login request
     * @return the generated authentication response
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(response);
    }
}
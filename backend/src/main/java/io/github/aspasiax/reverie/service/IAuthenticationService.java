package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.dto.auth.AuthResponse;
import io.github.aspasiax.reverie.dto.auth.LoginRequest;
import io.github.aspasiax.reverie.dto.auth.RegisterRequest;

/**
 * Defines authentication operations for user registration
 * and login.
 */
public interface IAuthenticationService {

    /**
     * Registers a new user account.
     *
     * @param request the registration request
     * @return the generated authentication response
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates an existing user.
     *
     * @param request the login request
     * @return the generated authentication response
     */
    AuthResponse login(LoginRequest request);
}
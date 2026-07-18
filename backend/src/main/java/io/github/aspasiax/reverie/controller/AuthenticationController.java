package io.github.aspasiax.reverie.controller;

import io.github.aspasiax.reverie.dto.auth.AuthResponse;
import io.github.aspasiax.reverie.dto.auth.LoginRequest;
import io.github.aspasiax.reverie.dto.auth.RegisterRequest;
import io.github.aspasiax.reverie.service.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes public authentication endpoints for user registration
 * and login.
 *
 * <p>
 * Both endpoints are accessible without an existing JWT token.
 * Successful authentication returns a JWT access token that may
 * be used to access protected application resources.
 * </p>
 */
@Tag(
        name = "Authentication",
        description = "Operations for user registration and secure JWT authentication."
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    /**
     * Registers a new user account.
     *
     * <p>
     * The submitted registration data is validated before the user
     * account is created. A successful registration returns a JWT
     * access token for the newly created account.
     * </p>
     *
     * @param request the validated registration request
     * @return the generated authentication response
     */
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns a JWT access token.",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid registration data"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Username or email already exists"
            )
    })
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
     * <p>
     * The supplied email and password are verified against the
     * stored user credentials. Successful authentication returns
     * a JWT access token.
     * </p>
     *
     * @param request the validated login request
     * @return the generated authentication response
     */
    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user using email and password and returns a JWT access token.",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid login data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(response);
    }
}
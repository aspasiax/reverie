package io.github.aspasiax.reverie.security.config;

import io.github.aspasiax.reverie.security.jwt.JwtAuthenticationFilter;
import io.github.aspasiax.reverie.security.userdetails.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configures authentication and request-level security for Reverie.
 *
 * <p>The application uses database-backed authentication, BCrypt
 * password hashing and stateless JWT access tokens.</p>
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Provides the encoder used to hash and verify user passwords.
     *
     * @return the BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures database-backed email and password authentication.
     *
     * @return the authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(customUserDetailsService);

        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    /**
     * Exposes Spring Security's authentication manager as a bean.
     *
     * @param configuration the authentication configuration
     * @return the configured authentication manager
     * @throws Exception if the manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Defines the HTTP security rules and JWT filter ordering.
     *
     * <p>Authentication endpoints remain public, while all other
     * endpoints require authentication. The JWT filter runs before
     * Spring Security's username and password authentication filter.</p>
     *
     * @param http the HTTP security builder
     * @return the configured security filter chain
     * @throws Exception if the configuration cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
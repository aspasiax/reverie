package io.github.aspasiax.reverie.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configures cross-origin resource sharing for the Reverie API.
 *
 * <p>The React frontend runs on a different origin than the API during
 * development, so the browser blocks its requests unless the API
 * explicitly allows that origin.</p>
 */
@Configuration
public class CorsConfig {

    /**
     * Frontend origins allowed to call the API.
     */
    @Value("${allowed.origins:http://localhost:5173}")
    private List<String> allowedOrigins;

    /**
     * Defines the CORS rules applied to every API endpoint.
     *
     * @return the CORS configuration source used by the security filter chain
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type"
        ));

        // Caches the preflight response so the browser does not repeat it.
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
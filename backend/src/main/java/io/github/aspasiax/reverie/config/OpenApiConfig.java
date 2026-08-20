package io.github.aspasiax.reverie.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for the Reverie application.
 *
 * <p>
 * This configuration customizes the generated API documentation
 * displayed through Swagger UI.
 * </p>
 *
 * <p>
 * It defines the main metadata of the Reverie REST API, including
 * its title, description and current version.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the customized OpenAPI definition used by Swagger UI.
     *
     * <p>
     * The generated documentation allows developers to explore
     * the available endpoints, inspect request and response schemas
     * and test the REST API directly from the browser.
     * </p>
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI reverieOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Reverie API")
                        .description("""
                            REST API for Reverie, a movie tracking application.
                        
                            Reverie allows users to:
                        
                            • Discover movies
                            • Keep a personal watch history
                            • Write and manage movie reviews
                            • Organize their movie journey
                            • Authenticate securely using JWT
                        
                            ----------------------------------------
                        
                            Demo Credentials
                        
                            Administrator
                            • Email: admin@reverie.com
                            • Password: Admin123!
                        
                            Regular User
                            • Email: emma@reverie.com
                            • Password: User123!
                        
                            Use one of the accounts above to authenticate,
                            obtain a JWT access token and test the secured endpoints.
                            """)
                        .version("1.0.0"))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }
}
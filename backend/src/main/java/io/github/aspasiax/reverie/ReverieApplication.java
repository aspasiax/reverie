package io.github.aspasiax.reverie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Starts the Reverie application.
 *
 * <p>JPA auditing is switched on here rather than in a configuration class
 * of its own, because it is the only thing this application asks Spring for
 * beyond the defaults. It is what fills the {@code createdAt} and
 * {@code updatedAt} fields every entity inherits: nothing in the services
 * ever sets them, and without this annotation they would silently stay
 * null.</p>
 */
@SpringBootApplication
@EnableJpaAuditing
public class ReverieApplication {

    /**
     * Hands control to Spring Boot.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ReverieApplication.class, args);
    }
}
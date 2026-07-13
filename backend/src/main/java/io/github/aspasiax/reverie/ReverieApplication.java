package io.github.aspasiax.reverie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ReverieApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReverieApplication.class, args);
	}
}
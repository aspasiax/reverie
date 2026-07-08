package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to users.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their public UUID.
     *
     * @param uuid the user UUID
     * @return the matching user, if found
     */
    Optional<User> findByUuid(UUID uuid);

    /**
     * Finds a user by their unique username.
     *
     * @param username the username
     * @return the matching user, if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their unique email address.
     *
     * @param email the email address
     * @return the matching user, if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a username is already used.
     *
     * @param username the username to check
     * @return {@code true} if the username already exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether an email address is already used.
     *
     * @param email the email address to check
     * @return {@code true} if the email already exists
     */
    boolean existsByEmail(String email);
}
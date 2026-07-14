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
     * Finds a user by their username, ignoring letter case.
     *
     * @param username the username
     * @return the matching user, if found
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Finds a user by their email address, ignoring letter case.
     *
     * @param email the email address
     * @return the matching user, if found
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Checks whether a user with the given public UUID exists.
     *
     * @param uuid the user UUID
     * @return {@code true} if a user exists with the given UUID
     */
    boolean existsByUuid(UUID uuid);

    /**
     * Checks whether a username is already used, ignoring letter case.
     *
     * @param username the username to check
     * @return {@code true} if the username already exists
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Checks whether an email address is already used, ignoring letter case.
     *
     * @param email the email address to check
     * @return {@code true} if the email already exists
     */
    boolean existsByEmailIgnoreCase(String email);
}
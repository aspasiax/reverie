package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines the business operations related to users.
 *
 * <p>This interface acts as the contract between the application
 * layer and the persistence layer for user management.</p>
 */
public interface IUserService {

    /**
     * Returns all registered users.
     *
     * @return a list containing all users
     */
    List<User> findAll();

    /**
     * Finds a user by its internal database identifier.
     *
     * @param id the user id
     * @return the matching user, if found
     */
    Optional<User> findById(Long id);

    /**
     * Finds a user by its public UUID.
     *
     * @param uuid the user UUID
     * @return the matching user, if found
     */
    Optional<User> findByUuid(UUID uuid);

    /**
     * Finds a user by username.
     *
     * @param username the username
     * @return the matching user, if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email address.
     *
     * @param email the email address
     * @return the matching user, if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Persists a new user.
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Deletes a user by its internal identifier.
     *
     * @param id the user id
     */
    void deleteById(Long id);
}
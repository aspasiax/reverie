package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
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
     * Finds an active user by their public UUID.
     *
     * @param uuid the user UUID
     * @return the matching active user, if found
     */
    Optional<User> findByUuidAndDeletedFalse(UUID uuid);

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
     * Checks whether an active user already uses the given username,
     * ignoring letter case.
     *
     * <p>Soft-deleted users are excluded, so a username becomes available
     * again once the account that held it is deleted.</p>
     *
     * @param username the username to check
     * @return {@code true} if an active user uses the username
     */
    boolean existsByUsernameIgnoreCaseAndDeletedFalse(String username);

    /**
     * Checks whether an active user already uses the given email address,
     * ignoring letter case.
     *
     * <p>Soft-deleted users are excluded, so an email address becomes available
     * again once the account that held it is deleted.</p>
     *
     * @param email the email address to check
     * @return {@code true} if an active user uses the email address
     */
    boolean existsByEmailIgnoreCaseAndDeletedFalse(String email);

    /**
     * Returns all users that have not been soft deleted.
     *
     * @return the active users ordered alphabetically by username
     */
    List<User> findAllByDeletedFalseOrderByUsernameAsc();

    /**
     * Counts the accounts that cannot sign in.
     *
     * @return how many accounts are disabled
     */
    long countByEnabledFalse();

    /**
     * Returns the users who have recorded the most viewings.
     *
     * <p>The count comes from a subquery rather than a join, so a user who
     * has watched nothing still appears, at the end of the list.</p>
     *
     * @param pageable how many to return
     * @return users ordered by how much they have watched
     */
    @Query("""
            SELECT u FROM User u
            WHERE u.deleted = FALSE
            ORDER BY (
                SELECT COUNT(w) FROM WatchLog w
                WHERE w.user = u AND w.deleted = FALSE
            ) DESC, u.displayName ASC
            """)
    List<User> findMostActive(Pageable pageable);
}
package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its public UUID.
     *
     * @param uuid the role UUID
     * @return the matching role, if found
     */
    Optional<Role> findByUuid(UUID uuid);

    /**
     * Finds a role by its unique name.
     *
     * @param name the role name
     * @return the matching role, if found
     */
    Optional<Role> findByName(String name);

    /**
     * Checks whether a role with the given name already exists.
     *
     * @param name the role name
     * @return {@code true} if a role exists with the given name
     */
    boolean existsByName(String name);
}
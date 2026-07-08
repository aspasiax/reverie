package io.github.aspasiax.reverie.repository;

import io.github.aspasiax.reverie.domain.Capability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository responsible for database operations related to capabilities.
 */
public interface CapabilityRepository extends JpaRepository<Capability, Long> {

    /**
     * Finds a capability by its public UUID.
     *
     * @param uuid the capability UUID
     * @return the matching capability, if found
     */
    Optional<Capability> findByUuid(UUID uuid);

    /**
     * Finds a capability by its unique name.
     *
     * @param name the capability name
     * @return the matching capability, if found
     */
    Optional<Capability> findByName(String name);

    /**
     * Checks whether a capability with the given name already exists.
     *
     * @param name the capability name
     * @return {@code true} if a capability exists with the given name
     */
    boolean existsByName(String name);
}
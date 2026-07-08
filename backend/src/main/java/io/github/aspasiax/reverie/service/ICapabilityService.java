package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Capability;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines the business operations related to capabilities.
 *
 * <p>This interface acts as the contract between the application
 * layer and the persistence layer for capability management.</p>
 */
public interface ICapabilityService {

    /**
     * Returns all capabilities.
     *
     * @return a list containing all capabilities
     */
    List<Capability> findAll();

    /**
     * Finds a capability by its internal database identifier.
     *
     * @param id the capability id
     * @return the matching capability, if found
     */
    Optional<Capability> findById(Long id);

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
     * Persists a new capability.
     *
     * @param capability the capability to save
     * @return the saved capability
     */
    Capability save(Capability capability);

    /**
     * Deletes a capability by its internal identifier.
     *
     * @param id the capability id
     */
    void deleteById(Long id);
}
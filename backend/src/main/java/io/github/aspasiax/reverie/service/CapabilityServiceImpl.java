package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.Capability;
import io.github.aspasiax.reverie.repository.CapabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link ICapabilityService}.
 *
 * <p>Delegates persistence operations to the
 * {@link CapabilityRepository}. Additional business rules
 * will be introduced as the application evolves.</p>
 */
@Service
@RequiredArgsConstructor
public class CapabilityServiceImpl implements ICapabilityService {

    private final CapabilityRepository capabilityRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Capability> findAll() {
        return capabilityRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Capability> findById(Long id) {
        return capabilityRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Capability> findByUuid(UUID uuid) {
        return capabilityRepository.findByUuid(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Capability> findByName(String name) {
        return capabilityRepository.findByName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Capability save(Capability capability) {
        return capabilityRepository.save(capability);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        capabilityRepository.deleteById(id);
    }
}
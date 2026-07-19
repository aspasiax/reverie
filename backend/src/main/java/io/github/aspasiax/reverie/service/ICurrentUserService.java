package io.github.aspasiax.reverie.service;

import io.github.aspasiax.reverie.domain.User;

/**
 * Provides access to the currently authenticated user.
 */
public interface ICurrentUserService {

    /**
     * Retrieves the currently authenticated user.
     *
     * @return the authenticated user entity
     */
    User getCurrentUser();

}
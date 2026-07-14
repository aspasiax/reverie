-- =========================================================
-- V7: Create role_capabilities join table
-- =========================================================
-- Stores the many-to-many relationship between roles and
-- capabilities. A role can have multiple capabilities and a
-- capability can belong to multiple roles.
-- =========================================================

CREATE TABLE role_capabilities
(
    role_id BIGINT NOT NULL,
    capability_id BIGINT NOT NULL,

    CONSTRAINT pk_role_capabilities PRIMARY KEY (role_id, capability_id),

    CONSTRAINT fk_role_capabilities_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_role_capabilities_capability
        FOREIGN KEY (capability_id)
            REFERENCES capabilities (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_role_capabilities_capability_id
    ON role_capabilities (capability_id);
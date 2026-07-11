package io.github.aspasiax.reverie.security.userdetails;

import io.github.aspasiax.reverie.domain.Capability;
import io.github.aspasiax.reverie.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Adapts the Reverie {@link User} entity to Spring Security's
 * {@link UserDetails} contract.
 *
 * <p>The authenticated user's role and capabilities are converted
 * into granted authorities and used during authorization checks.</p>
 */
public class CustomUserDetails implements UserDetails {

    private final UUID uuid;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final boolean deleted;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Creates security details from a Reverie user.
     *
     * @param user the persisted user entity
     */
    public CustomUserDetails(User user) {
        this.uuid = user.getUuid();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.enabled = user.isEnabled();
        this.deleted = user.isDeleted();
        this.authorities = mapAuthorities(user);
    }

    /**
     * Converts the user's role and capabilities into Spring Security
     * authorities.
     *
     * <p>The role receives the conventional {@code ROLE_} prefix,
     * while capability names are used directly.</p>
     *
     * @param user the user whose permissions will be mapped
     * @return the granted authorities of the user
     */
    private Collection<? extends GrantedAuthority> mapAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        String roleName = user.getRole().getName();

        if (roleName.startsWith("ROLE_")) {
            authorities.add(new SimpleGrantedAuthority(roleName));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
        }

        for (Capability capability : user.getRole().getCapabilities()) {
            authorities.add(new SimpleGrantedAuthority(capability.getName()));
        }

        return Set.copyOf(authorities);
    }

    /**
     * Returns the public UUID of the authenticated user.
     *
     * @return the user UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns the authorities granted to the authenticated user.
     *
     * @return the role and capability authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the stored encoded password.
     *
     * @return the encoded password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the email used as the authentication username.
     *
     * <p>Although Spring Security names this method
     * {@code getUsername()}, Reverie authenticates users using email.</p>
     *
     * @return the user email
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Indicates whether the account has expired.
     *
     * @return always {@code true}, because account expiration is not
     * currently supported
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account is locked.
     *
     * @return always {@code true}, because account locking is not
     * currently supported
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the credentials have expired.
     *
     * @return always {@code true}, because credential expiration is not
     * currently supported
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the account may authenticate.
     *
     * <p>A user must be enabled and must not be soft deleted.</p>
     *
     * @return {@code true} when the account is active
     */
    @Override
    public boolean isEnabled() {
        return enabled && !deleted;
    }
}
package io.github.aspasiax.reverie.security.jwt;

import io.github.aspasiax.reverie.security.userdetails.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Provides operations for creating, reading and validating
 * JWT access tokens used by the Reverie application.
 *
 * <p>Each token is signed using an HMAC secret key and contains
 * the authenticated user's UUID, email address and granted
 * authorities.</p>
 */
@Service
public class JwtService {

    private final String secret;
    private final long expirationMs;

    /**
     * Creates the JWT service using values loaded from the
     * application configuration.
     *
     * @param secret       the Base64-encoded secret used to sign tokens
     * @param expirationMs the token lifetime in milliseconds
     */
    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a signed JWT access token for an authenticated user.
     *
     * <p>The user's UUID is stored as the token subject. The email
     * address and granted authorities are stored as additional claims.</p>
     *
     * @param userDetails the authenticated user's security details
     * @return the generated JWT access token
     */
    public String generateToken(CustomUserDetails userDetails) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(expirationMs);

        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUuid().toString())
                .claim("email", userDetails.getUsername())
                .claim("authorities", authorities)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts the public user UUID stored in the token subject.
     *
     * @param token the JWT access token
     * @return the authenticated user's UUID
     */
    public UUID extractUserUuid(String token) {
        return UUID.fromString(extractSubject(token));
    }

    /**
     * Extracts the user's email address from the token.
     *
     * @param token the JWT access token
     * @return the email stored in the token claims
     */
    public String extractEmail(String token) {
        return extractClaim(
                token,
                claims -> claims.get("email", String.class)
        );
    }

    /**
     * Extracts the token subject.
     *
     * @param token the JWT access token
     * @return the token subject
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the token expiration timestamp.
     *
     * @param token the JWT access token
     * @return the token expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific value from the token claims.
     *
     * @param token          the JWT access token
     * @param claimsResolver the function used to retrieve the desired claim
     * @param <T>            the type of the extracted value
     * @return the extracted claim value
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validates that the token belongs to the supplied user
     * and has not expired.
     *
     * <p>The token signature is also verified automatically while
     * its claims are parsed.</p>
     *
     * @param token       the JWT access token
     * @param userDetails the expected authenticated user
     * @return {@code true} if the token is valid
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        String email = extractEmail(token);

        return email.equalsIgnoreCase(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * Returns the configured token lifetime.
     *
     * @return the token expiration duration in milliseconds
     */
    public long getExpirationMs() {
        return expirationMs;
    }

    /**
     * Parses the token and verifies its digital signature.
     *
     * @param token the JWT access token
     * @return all verified claims contained in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks whether the token expiration timestamp is in the past.
     *
     * @param token the JWT access token
     * @return {@code true} if the token has expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Converts the configured Base64 secret into the cryptographic
     * key used to sign and verify JWT tokens.
     *
     * @return the HMAC signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
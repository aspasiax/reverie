package io.github.aspasiax.reverie.security.jwt;

import io.github.aspasiax.reverie.security.userdetails.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Inspects incoming HTTP requests for JWT access tokens.
 *
 * <p>The filter executes once per request. When a valid Bearer token
 * is present, it loads the corresponding user, validates the token
 * and establishes the authenticated user in Spring Security's
 * security context.</p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Inspects an incoming request and attempts to authenticate the user
     * when a valid JWT access token is present.
     *
     * <p>If the request does not contain a Bearer token, the filter
     * allows the request to continue without establishing authentication.
     * The security rules defined in {@code SecurityConfig} later decide
     * whether unauthenticated access is permitted.</p>
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain the remaining filters in the security chain
     * @throws ServletException if request processing fails
     * @throws IOException      if an input or output operation fails
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        /*
         * Requests without a valid Bearer header are not processed
         * by this filter and continue through the remaining chain.
         */
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Removes the "Bearer " prefix and keeps only the compact JWT.
        String jwt = authorizationHeader.substring(7);
        String email = jwtService.extractEmail(jwt);

        /*
         * Authentication is created only when the token identifies
         * a user and no previous filter has already authenticated
         * the current request.
         */
        if (email != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails =
                    customUserDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
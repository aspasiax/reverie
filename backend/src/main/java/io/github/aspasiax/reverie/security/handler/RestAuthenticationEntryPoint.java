package io.github.aspasiax.reverie.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aspasiax.reverie.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Returns a {@code 401 Unauthorized} JSON response when an unauthenticated
 * request attempts to access a protected resource.
 *
 * <p>Without this entry point Spring Security falls back to
 * {@code Http403ForbiddenEntryPoint}, which answers unauthenticated
 * requests with {@code 403 Forbidden} and an empty body.</p>
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Writes the standard error payload for unauthenticated requests.
     *
     * @param request       the rejected HTTP request
     * @param response      the outgoing HTTP response
     * @param authException the authentication failure
     * @throws IOException if the response body cannot be written
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Authentication is required to access this resource.",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
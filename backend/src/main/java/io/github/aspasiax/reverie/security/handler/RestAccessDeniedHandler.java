package io.github.aspasiax.reverie.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aspasiax.reverie.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Returns a {@code 403 Forbidden} JSON response when an authenticated user
 * lacks the capability required by a protected resource.
 *
 * <p>This handler covers authorization failures raised inside the security
 * filter chain. Failures raised by method security annotations on
 * controllers are converted by the global exception handler instead.</p>
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Writes the standard error payload for authorization failures.
     *
     * @param request               the rejected HTTP request
     * @param response              the outgoing HTTP response
     * @param accessDeniedException the authorization failure
     * @throws IOException if the response body cannot be written
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "You do not have permission to perform this operation.",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
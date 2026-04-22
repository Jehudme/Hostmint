package com.hostmint.app.web.filter;

import com.hostmint.app.domain.RequestLog;
import com.hostmint.app.domain.enumeration.HttpMethod;
import com.hostmint.app.repository.RequestLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for intercepting and logging incoming HTTP requests to the database.
 * Inherits from OncePerRequestFilter to ensure a single execution per request dispatch.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final RequestLogRepository requestLogRepository;

    public RequestLoggingFilter(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        // Only log API requests to avoid overhead on static resources
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String correlationId = UUID.randomUUID().toString();

        // Attach the correlationId to the request so it can be retrieved elsewhere if needed (e.g., in logs or controllers)
        request.setAttribute("correlationId", correlationId);

        try {
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Execution continues here after the request has been processed
            long duration = System.currentTimeMillis() - startTime;
            saveRequestLog(request, response, correlationId, duration);
        }
    }

    private void saveRequestLog(HttpServletRequest request, HttpServletResponse response, String correlationId, long duration) {
        try {
            RequestLog requestLog = new RequestLog();

            requestLog.setCorrelationId(correlationId);
            requestLog.setPath(truncate(request.getRequestURI(), 500));
            requestLog.setStatusCode(response.getStatus());
            requestLog.setDurationMs(duration);
            requestLog.setIpAddress(request.getRemoteAddr());
            requestLog.setCreatedAt(Instant.now());

            // Map the HTTP method string to the HttpMethod Enum
            try {
                requestLog.setMethod(HttpMethod.valueOf(request.getMethod().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Handle non-standard or missing methods if the enum is not exhaustive
                log.warn("Unknown HTTP method encountered: {}", request.getMethod());
            }

            // Retrieve the authenticated user's login
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                requestLog.setPrincipal(truncate(auth.getName(), 255));
            }

            // Retrieve the specific error message captured by the ExceptionTranslator
            String specificError = (String) request.getAttribute("audit_error_message");

            if (response.getStatus() >= 400) {
                requestLog.setErrorCode("HTTP_" + response.getStatus());

                // If we found a specific message in the translator, use it.
                // Otherwise, fall back to a generic status message.
                if (specificError != null) {
                    requestLog.setErrorMessage(truncate(specificError, 1000));
                } else {
                    requestLog.setErrorMessage("Request failed with status " + response.getStatus());
                }
            }

            requestLogRepository.save(requestLog);
        } catch (Exception e) {
            // Critical: Logging logic must never interfere with the main application flow or cause a crash
            log.error("Failed to save RequestLog to database", e);
        }
    }

    /**
     * Utility method to prevent data truncation errors in the database.
     */
    private String truncate(String value, int length) {
        if (value == null) return null;
        return value.length() > length ? value.substring(0, length - 3) + "..." : value;
    }
}

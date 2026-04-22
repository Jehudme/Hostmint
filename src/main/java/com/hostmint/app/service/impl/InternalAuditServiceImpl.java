package com.hostmint.app.service.impl;

import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.service.AuditLogService;
import com.hostmint.app.service.InternalAuditService;
import com.hostmint.app.service.dto.AuditLogDTO;
import com.hostmint.app.service.dto.ProjectDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class InternalAuditServiceImpl implements InternalAuditService {

    private final Logger log = LoggerFactory.getLogger(InternalAuditServiceImpl.class);

    private final AuditLogService auditLogService;

    public InternalAuditServiceImpl(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Override
    public void log(String action, String entityName, LogLevel level, String message) {
        log(action, entityName, null, level, message, null, null);
    }

    @Override
    public void log(String action, String entityName, UUID entityId, LogLevel level, String message) {
        log(action, entityName, entityId, level, message, null, null);
    }

    @Override
    public void log(String action, String entityName, UUID entityId, LogLevel level, String message, ProjectDTO project) {
        log(action, entityName, entityId, level, message, project, null);
    }

    @Override
    public void log(String action, String entityName, UUID entityId, LogLevel level, String message, ProjectDTO project, String metadata) {
        try {
            AuditLogDTO dto = new AuditLogDTO();

            // 1. Map explicit business arguments
            dto.setAction(truncate(action, 120));
            dto.setEntityName(truncate(entityName, 120));
            dto.setEntityId(entityId);
            dto.setLevel(level);
            dto.setMessage(truncate(message, 1000));
            dto.setProject(project);
            dto.setMetadata(metadata);

            // 2. Auto-fill Timestamp
            dto.setCreatedAt(Instant.now());

            // 3. Auto-fill Security Context (Principal)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                dto.setPrincipal(truncate(auth.getName(), 255));
            } else {
                dto.setPrincipal("system");
            }

            // 4. Auto-fill Request Context (IP, User-Agent, CorrelationId)
            // This safely retrieves the current HTTP request from anywhere in the thread
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                dto.setIpAddress(truncate(request.getRemoteAddr(), 64));
                dto.setUserAgent(truncate(request.getHeader("User-Agent"), 500));

                // Catch the correlation ID you set earlier in RequestLoggingFilter
                String correlationId = (String) request.getAttribute("correlationId");
                dto.setCorrelationId(correlationId != null ? correlationId : UUID.randomUUID().toString());
            } else {
                // Fallback if called outside a web request (e.g., in a @Scheduled background job)
                dto.setCorrelationId(UUID.randomUUID().toString());
                dto.setIpAddress("127.0.0.1");
            }

            // 5. Persist to DB using the standard generated service
            auditLogService.save(dto);
        } catch (Exception e) {
            // Auditing failures should NEVER crash the main business logic
            log.error("Failed to create internal audit log for action: {}", action, e);
        }
    }

    /**
     * Utility method to prevent JPA constraint violations if a string is too long.
     */
    private String truncate(String value, int length) {
        if (value == null) return null;
        return value.length() > length ? value.substring(0, length - 3) + "..." : value;
    }
}

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
    public void log(String action, String entityName, LogLevel level, String message, ProjectDTO project) {
        // Here, entityId is null because Project uses a Long ID, but the project relationship is set
        log(action, entityName, null, level, message, project, null);
    }

    @Override
    public void log(String action, String entityName, UUID entityId, LogLevel level, String message) {
        log(action, entityName, entityId, level, message, null, null);
    }

    @Override
    public void log(String action, String entityName, UUID entityId, LogLevel level, String message, ProjectDTO project, String metadata) {
        try {
            AuditLogDTO dto = new AuditLogDTO();

            // 1. Core Business Fields
            dto.setAction(truncate(action, 120));
            dto.setEntityName(truncate(entityName, 120));
            dto.setEntityId(entityId);
            dto.setLevel(level);
            dto.setMessage(truncate(message, 1000));
            dto.setProject(project);
            dto.setMetadata(metadata);
            dto.setCreatedAt(Instant.now());

            // 2. Automated Security Context
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            dto.setPrincipal(auth != null && auth.isAuthenticated() ? truncate(auth.getName(), 255) : "system");

            // 3. Automated Web Request Context
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                dto.setIpAddress(truncate(request.getRemoteAddr(), 64));
                dto.setUserAgent(truncate(request.getHeader("User-Agent"), 500));

                String correlationId = (String) request.getAttribute("correlationId");
                dto.setCorrelationId(correlationId != null ? correlationId : UUID.randomUUID().toString());
            } else {
                dto.setIpAddress("127.0.0.1");
                dto.setCorrelationId(UUID.randomUUID().toString());
            }

            auditLogService.save(dto);
        } catch (Exception e) {
            log.error("Failed to save audit log for action: {}", action, e);
        }
    }

    private String truncate(String value, int length) {
        if (value == null) return null;
        return value.length() > length ? value.substring(0, length - 3) + "..." : value;
    }
}

package com.hostmint.app.service;

import com.hostmint.app.domain.enumeration.LogLevel;
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

/**
 * A professional wrapper for the JHipster AuditLogService.
 * Automatically handles Principal, IP Address, User-Agent, and Correlation IDs.
 */
@Service
public class InternalAuditLogger {

    private static final Logger LOG = LoggerFactory.getLogger(InternalAuditLogger.class);
    private final AuditLogService auditLogService;

    public InternalAuditLogger(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Starts a new fluent audit log builder sequence.
     * @param action The business action (e.g., "USER_REGISTERED", "PROJECT_DELETED")
     * @return AuditBuilder
     */
    public AuditBuilder action(String action) {
        return new AuditBuilder(action, this);
    }

    protected void saveLog(AuditLogDTO auditDto) {
        try {
            // 1. Automatically attach Security Context (Who did this?)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                auditDto.setPrincipal(authentication.getName());
            } else {
                auditDto.setPrincipal("SYSTEM");
            }

            // 2. Automatically attach HTTP Context (Where did they do it from?)
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                auditDto.setIpAddress(request.getRemoteAddr());
                auditDto.setUserAgent(request.getHeader("User-Agent"));
            }

            // 3. Attach temporal and tracing data
            auditDto.setCreatedAt(Instant.now());
            if (auditDto.getCorrelationId() == null) {
                auditDto.setCorrelationId(UUID.randomUUID().toString());
            }

            // 4. Save using the JHipster generated service
            auditLogService.save(auditDto);
        } catch (Exception e) {
            // Never let audit logging crash the main business transaction
            LOG.error("Failed to save internal audit log: {}", e.getMessage(), e);
        }
    }

    // ========================================================================
    // FLUENT BUILDER CLASS
    // ========================================================================
    public static class AuditBuilder {

        private final AuditLogDTO dto;
        private final InternalAuditLogger logger;

        private AuditBuilder(String action, InternalAuditLogger logger) {
            this.logger = logger;
            this.dto = new AuditLogDTO();
            this.dto.setAction(action);
        }

        public AuditBuilder target(String entityName, Long entityId) {
            this.dto.setEntityName(entityName);
            this.dto.setEntityId(entityId);
            return this;
        }

        public AuditBuilder project(ProjectDTO project) {
            this.dto.setProject(project);
            return this;
        }

        public AuditBuilder metadata(String jsonMetadata) {
            this.dto.setMetadata(jsonMetadata);
            return this;
        }

        public AuditBuilder correlationId(String correlationId) {
            this.dto.setCorrelationId(correlationId);
            return this;
        }

        // Terminal methods that trigger the actual save
        public void info(String message) {
            this.dto.setLevel(LogLevel.INFO);
            this.dto.setMessage(message);
            logger.saveLog(dto);
        }

        public void warn(String message) {
            this.dto.setLevel(LogLevel.WARN);
            this.dto.setMessage(message);
            logger.saveLog(dto);
        }

        public void error(String message) {
            this.dto.setLevel(LogLevel.ERROR);
            this.dto.setMessage(message);
            logger.saveLog(dto);
        }

        public void debug(String message) {
            this.dto.setLevel(LogLevel.DEBUG);
            this.dto.setMessage(message);
            logger.saveLog(dto);
        }
    }
}

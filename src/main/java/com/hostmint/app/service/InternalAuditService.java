package com.hostmint.app.service;

import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.service.dto.ProjectDTO;
import java.util.UUID;

/**
 * Service Interface for automating AuditLog creation.
 */
public interface InternalAuditService {
    // Basic log
    void log(String action, String entityName, LogLevel level, String message);

    // Automated log for Project actions (Matches your call in ExtendedProjectServiceImpl)
    void log(String action, String entityName, LogLevel level, String message, ProjectDTO project);

    // Log for entities using UUIDs
    void log(String action, String entityName, UUID entityId, LogLevel level, String message);

    // Full signature
    void log(String action, String entityName, UUID entityId, LogLevel level, String message, ProjectDTO project, String metadata);
}

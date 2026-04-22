package com.hostmint.app.service;

import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.service.dto.ProjectDTO;
import java.util.UUID;

/**
 * Service Interface for automating the creation of internal audit logs.
 * This abstracts away the boilerplate of fetching IPs, user agents, and security contexts.
 */
public interface InternalAuditService {
    /**
     * Logs an event with minimal required information.
     */
    void log(String action, String entityName, LogLevel level, String message);

    /**
     * Logs an event linked to a specific entity ID.
     */
    void log(String action, String entityName, UUID entityId, LogLevel level, String message);

    /**
     * Logs an event linked to an entity ID and a Project context.
     */
    void log(String action, String entityName, UUID entityId, LogLevel level, String message, ProjectDTO project);

    /**
     * Logs an event with full business details, including metadata.
     */
    void log(String action, String entityName, UUID entityId, LogLevel level, String message, ProjectDTO project, String metadata);
}

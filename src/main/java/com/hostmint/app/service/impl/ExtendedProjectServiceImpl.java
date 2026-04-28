package com.hostmint.app.service.impl;

import com.hostmint.app.aop.audit.Audit;
import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.service.AuditLogService;
import com.hostmint.app.service.InternalAuditService;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import java.util.UUID;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Primary
public class ExtendedProjectServiceImpl extends ProjectServiceImpl {

    private final ProjectRepository projectRepository;
    private final InternalAuditService internalAuditService;

    public ExtendedProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository,
        InternalAuditService internalAuditService
    ) {
        super(projectRepository, projectMapper, projectSearchRepository);
        this.projectRepository = projectRepository;
        this.internalAuditService = internalAuditService;
    }

    @Override
    @Audit(action = "PROJECT_CREATE", entity = "#result.name", entityId = "#result.id", message = "'Created project'", project = "#result")
    public ProjectDTO save(ProjectDTO projectDTO) {
        ProjectDTO savedResult = super.save(projectDTO);
        return savedResult;
    }

    @Override
    @Audit(action = "PROJECT_DELETE", entity = "'Project'", entityId = "#id", level = "WARN", message = "'Cannot delete project'")
    public void delete(UUID id) {
        // Functionality not available
    }
}

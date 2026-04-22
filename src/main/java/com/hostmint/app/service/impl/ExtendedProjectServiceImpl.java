package com.hostmint.app.service.impl;

import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.service.InternalAuditService;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Primary
public class ExtendedProjectServiceImpl extends ProjectServiceImpl {

    private final InternalAuditService internalAuditService;

    public ExtendedProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository,
        InternalAuditService internalAuditService
    ) {
        super(projectRepository, projectMapper, projectSearchRepository);
        this.internalAuditService = internalAuditService;
    }

    @Override
    public ProjectDTO save(ProjectDTO projectDTO) {
        ProjectDTO result = super.save(projectDTO);
        internalAuditService.log("PROJECT_CREATED", "Project", LogLevel.INFO, "Created: " + result.getName(), result);
        return result;
    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO) {
        ProjectDTO result = super.update(projectDTO);
        internalAuditService.log("PROJECT_UPDATED", "Project", LogLevel.INFO, "Updated: " + result.getName(), result);
        return result;
    }

    @Override
    public void delete(Long id) {
        // Fetch project name before it's gone for the log
        findOne(id).ifPresent(p ->
            internalAuditService.log("PROJECT_DELETED", "Project", LogLevel.WARN, "Deleted project: " + p.getName(), p)
        );
        super.delete(id);
    }
}

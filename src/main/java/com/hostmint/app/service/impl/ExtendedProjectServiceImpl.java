package com.hostmint.app.service.impl;

import com.hostmint.app.aop.audit.Audit;
import com.hostmint.app.domain.enumeration.LogLevel;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Primary
public class ExtendedProjectServiceImpl extends ProjectServiceImpl {

    public ExtendedProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        super(projectRepository, projectMapper, projectSearchRepository);
    }

    @Override
    @Audit(
        action = "PROJECT_CREATED",
        entity = "#result.name", // Captures real name from the saved object
        entityId = "#result.id", // Captures the new ID after generation
        message = "'Successfully created project'",
        project = "#result" // Passes the saved DTO
    )
    public ProjectDTO save(ProjectDTO projectDTO) {
        return super.save(projectDTO);
    }

    @Override
    @Audit(
        action = "PROJECT_UPDATED",
        entity = "#projectDTO.name", // Uses name from the request
        entityId = "#projectDTO.id",
        message = "'Updated project details'",
        project = "#projectDTO"
    )
    public ProjectDTO update(ProjectDTO projectDTO) {
        return super.update(projectDTO);
    }

    @Override
    @Audit(
        action = "PROJECT_DELETED",
        entity = "'Project'", // Since it's deleted, we use a static string
        entityId = "#id", // Captures the Long ID passed to the method
        level = LogLevel.WARN,
        message = "'Permanently removed project'"
    )
    public void delete(Long id) {
        super.delete(id);
    }
}

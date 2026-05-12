package com.hostmint.app.service.primary;

import com.hostmint.app.aop.audit.Auditable;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.service.ProjectService;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrimaryProjectService extends ProjectService {

    public PrimaryProjectService(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        super(projectRepository, projectMapper, projectSearchRepository);
    }

    @Override
    @Auditable(
        action = "PROJECT_CREATED",
        entityName = "Project",
        entityIdExpression = "#result.id",
        message = "A new project was created."
    )
    public ProjectDTO save(ProjectDTO projectDTO) {
        return super.save(projectDTO);
    }

    @Override
    @Auditable(
        action = "PROJECT_UPDATED",
        entityName = "Project",
        entityIdExpression = "#projectDTO.id",
        message = "A project was updated."
    )
    public ProjectDTO update(ProjectDTO projectDTO) {
        return super.update(projectDTO);
    }

    @Override
    @Auditable(action = "PROJECT_DELETED", entityName = "Project", entityIdExpression = "#id", message = "A project was deleted.")
    public void delete(Long id) {
        super.delete(id);
    }
}

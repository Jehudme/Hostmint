package com.hostmint.app.service.impl;

import com.hostmint.app.aop.audit.Audit;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
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

    // 1. Add this field so we can call flush()
    private final ProjectRepository projectRepository;

    public ExtendedProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        super(projectRepository, projectMapper, projectSearchRepository);
        // 2. Assign it
        this.projectRepository = projectRepository;
    }

    @Override
    @Audit(action = "PROJECT_CREATE", entity = "#result.name", entityId = "#result.id", message = "'Created project'", project = "#result")
    public ProjectDTO save(ProjectDTO projectDTO) {
        // 3. Save it normally (queues the insert)
        ProjectDTO savedResult = super.save(projectDTO);

        // 4. THE MAGIC LINE: Force Hibernate to push the insert to the database NOW
        projectRepository.flush();

        return savedResult;
    }

    @Override
    @Audit(action = "PROJECT_DELETE", entity = "'Project'", entityId = "#id", level = "WARN", message = "'Deleted project permanently'")
    public void delete(UUID id) {
        // Note: ensure this ID type matches your UUID refactor if needed
        super.delete(id);
        projectRepository.flush(); // Good practice to flush here too for the same reason
    }
}

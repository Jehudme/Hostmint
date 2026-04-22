package com.hostmint.app.service.impl;

import com.hostmint.app.domain.Project;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedProjectServiceImpl extends ProjectServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final ProjectSearchRepository projectSearchRepository;

    public ExtendedProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        super(projectRepository, projectMapper, projectSearchRepository);
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.projectSearchRepository = projectSearchRepository;
    }

    @Override
    public ProjectDTO save(ProjectDTO projectDTO) {
        return super.save(projectDTO);
    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO) {
        return super.update(projectDTO);
    }

    @Override
    public Optional<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        return super.partialUpdate(projectDTO);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }
}

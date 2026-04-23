package com.hostmint.app.service.impl;

import com.hostmint.app.domain.Project;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.service.ProjectService;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.hostmint.app.domain.Project}.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final ProjectSearchRepository projectSearchRepository;

    public ProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.projectSearchRepository = projectSearchRepository;
    }

    @Override
    public ProjectDTO save(ProjectDTO projectDTO) {
        LOG.debug("Request to save Project : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);
        projectSearchRepository.index(project);
        return projectMapper.toDto(project);
    }

    @Override
    public ProjectDTO update(ProjectDTO projectDTO) {
        LOG.debug("Request to update Project : {}", projectDTO);
        Project project = projectMapper.toEntity(projectDTO);
        project = projectRepository.save(project);
        projectSearchRepository.index(project);
        return projectMapper.toDto(project);
    }

    @Override
    public Optional<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        LOG.debug("Request to partially update Project : {}", projectDTO);

        return projectRepository
            .findById(projectDTO.getId())
            .map(existingProject -> {
                projectMapper.partialUpdate(existingProject, projectDTO);

                return existingProject;
            })
            .map(projectRepository::save)
            .map(savedProject -> {
                projectSearchRepository.index(savedProject);
                return savedProject;
            })
            .map(projectMapper::toDto);
    }

    public Page<ProjectDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projectRepository.findAllWithEagerRelationships(pageable).map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProjectDTO> findOne(Long id) {
        LOG.debug("Request to get Project : {}", id);
        return projectRepository.findOneWithEagerRelationships(id).map(projectMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Project : {}", id);
        projectRepository.deleteById(id);
        projectSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Projects for query {}", query);
        return projectSearchRepository.search(query, pageable).map(projectMapper::toDto);
    }
}

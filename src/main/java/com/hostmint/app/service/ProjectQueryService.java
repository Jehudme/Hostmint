package com.hostmint.app.service;

import com.hostmint.app.domain.*; // for static metamodels
import com.hostmint.app.domain.Project;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.service.criteria.ProjectCriteria;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Project} entities in the database.
 * The main input is a {@link ProjectCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProjectDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProjectQueryService extends QueryService<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectQueryService.class);

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final ProjectSearchRepository projectSearchRepository;

    public ProjectQueryService(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.projectSearchRepository = projectSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link ProjectDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProjectDTO> findByCriteria(ProjectCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.findAll(specification, page).map(projectMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProjectCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Project> specification = createSpecification(criteria);
        return projectRepository.count(specification);
    }

    /**
     * Function to convert {@link ProjectCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Project> createSpecification(ProjectCriteria criteria) {
        Specification<Project> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Project_.id),
                buildStringSpecification(criteria.getName(), Project_.name),
                buildStringSpecification(criteria.getProjectKey(), Project_.projectKey),
                buildSpecification(criteria.getDeleted(), Project_.deleted),
                buildRangeSpecification(criteria.getCreatedAt(), Project_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Project_.updatedAt),
                buildSpecification(criteria.getOwnerId(), root -> root.join(Project_.owner, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}

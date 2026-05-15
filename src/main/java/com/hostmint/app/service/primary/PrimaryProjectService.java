package com.hostmint.app.service.primary;

import com.hostmint.app.aop.audit.Auditable;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.security.AuthoritiesConstants;
import com.hostmint.app.security.SecurityUtils;
import com.hostmint.app.service.ProjectService;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.mapper.ProjectMapper;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrimaryProjectService extends ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public PrimaryProjectService(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        super(projectRepository, projectMapper, projectSearchRepository);
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    // ===================================================================================
    // SECURITY OVERRIDES (Ownership Filtering)
    // ===================================================================================

    @Override
    public Page<ProjectDTO> findAllWithEagerRelationships(Pageable pageable) {
        // Admins can see all projects
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return super.findAllWithEagerRelationships(pageable);
        }

        // Regular users only see projects they own
        // Note: You still need to add this method to your ProjectRepository interface:
        // @Query("select project from Project project left join fetch project.owner where project.owner.login = ?#{principal.username}")
        // Page<Project> findByOwnerIsCurrentUser(Pageable pageable);
        return projectRepository.findByOwnerIsCurrentUser(pageable).map(projectMapper::toDto);
    }

    @Override
    public Optional<ProjectDTO> findOne(Long id) {
        Optional<ProjectDTO> project = super.findOne(id);

        // Admins can view any single project
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            return project;
        }

        // If not admin, ensure the logged-in user matches the project's owner
        return project.filter(p -> {
            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElse("");
            return p.getOwner() != null && currentUserLogin.equals(p.getOwner().getLogin());
        });
    }

    // ===================================================================================
    // AUDITING OVERRIDES
    // ===================================================================================

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
    public Optional<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        // If you want auditing on partial updates, you can add the @Auditable annotation here as well
        return super.partialUpdate(projectDTO);
    }

    @Override
    @Auditable(action = "PROJECT_DELETED", entityName = "Project", entityIdExpression = "#id", message = "A project was deleted.")
    public void delete(Long id) {
        super.delete(id);
    }
}

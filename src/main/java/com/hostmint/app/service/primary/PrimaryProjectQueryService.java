package com.hostmint.app.service.primary;

import com.hostmint.app.domain.Project;
import com.hostmint.app.repository.ProjectRepository;
import com.hostmint.app.repository.search.ProjectSearchRepository;
import com.hostmint.app.security.AuthoritiesConstants;
import com.hostmint.app.security.SecurityUtils;
import com.hostmint.app.service.ProjectQueryService;
import com.hostmint.app.service.criteria.ProjectCriteria;
import com.hostmint.app.service.mapper.ProjectMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrimaryProjectQueryService extends ProjectQueryService {

    public PrimaryProjectQueryService(
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        ProjectSearchRepository projectSearchRepository
    ) {
        super(projectRepository, projectMapper, projectSearchRepository);
    }

    @Override
    protected Specification<Project> createSpecification(ProjectCriteria criteria) {
        // 1. Get the base specification from the generated superclass (handles normal UI filters)
        Specification<Project> specification = super.createSpecification(criteria);

        // 2. If the user is NOT an admin, append a strict security filter
        if (!SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)) {
            String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow();

            // This specification forces the SQL query to only match projects where owner.login == currentUserLogin
            Specification<Project> ownerSpec = (root, query, builder) -> builder.equal(root.join("owner").get("login"), currentUserLogin);

            // Append the security restriction using logical AND
            specification = specification.and(ownerSpec);
        }

        return specification;
    }
}

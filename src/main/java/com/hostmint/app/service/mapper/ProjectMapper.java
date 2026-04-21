package com.hostmint.app.service.mapper;

import com.hostmint.app.domain.Project;
import com.hostmint.app.domain.User;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "userLogin")
    ProjectDTO toDto(Project s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}

package com.hostmint.app.service.mapper;

import com.hostmint.app.domain.Project;
import com.hostmint.app.domain.RequestLog;
import com.hostmint.app.domain.User;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.dto.RequestLogDTO;
import com.hostmint.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RequestLog} and its DTO {@link RequestLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface RequestLogMapper extends EntityMapper<RequestLogDTO, RequestLog> {
    @Mapping(target = "actor", source = "actor", qualifiedByName = "userLogin")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectProjectKey")
    RequestLogDTO toDto(RequestLog s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("projectProjectKey")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "projectKey", source = "projectKey")
    ProjectDTO toDtoProjectProjectKey(Project project);
}

package com.hostmint.app.service.mapper;

import com.hostmint.app.domain.AuditLog;
import com.hostmint.app.domain.Project;
import com.hostmint.app.domain.User;
import com.hostmint.app.service.dto.AuditLogDTO;
import com.hostmint.app.service.dto.ProjectDTO;
import com.hostmint.app.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AuditLog} and its DTO {@link AuditLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditLogMapper extends EntityMapper<AuditLogDTO, AuditLog> {
    @Mapping(target = "actor", source = "actor", qualifiedByName = "userLogin")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectProjectKey")
    AuditLogDTO toDto(AuditLog s);

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

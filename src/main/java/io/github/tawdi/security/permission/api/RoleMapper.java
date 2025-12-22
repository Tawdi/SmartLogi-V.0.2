package io.github.tawdi.security.permission.api;

import io.github.tawdi.security.permission.domain.Role;
import org.mapstruct.Mapper;

@Mapper
public interface RoleMapper {

    RoleDTO toDto(Role entity);

    Role toEntity(RoleDTO dto);
}

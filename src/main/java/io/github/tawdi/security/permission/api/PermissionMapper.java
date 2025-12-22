package io.github.tawdi.security.permission.api;

import io.github.tawdi.security.permission.domain.Permission;
import org.mapstruct.*;

@Mapper
public interface PermissionMapper {

    PermissionDTO toDto(Permission entity);

    Permission toEntity(PermissionDTO dto);
}

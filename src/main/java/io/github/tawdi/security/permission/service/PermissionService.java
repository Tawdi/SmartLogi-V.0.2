package io.github.tawdi.security.permission.service;


import io.github.tawdi.security.permission.api.PermissionMapper;
import io.github.tawdi.security.permission.api.RoleMapper;
import io.github.tawdi.security.permission.domain.Permission;
import io.github.tawdi.security.permission.domain.Role;
import io.github.tawdi.security.permission.api.PermissionDTO;
import io.github.tawdi.security.permission.api.RoleDTO;
import io.github.tawdi.security.permission.repository.PermissionRepository;
import io.github.tawdi.security.permission.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;

    // Gestion des permissions
    public PermissionDTO createPermission(PermissionDTO dto) {
        Permission permission = Permission.builder()
                .code(dto.getCode().toUpperCase())
                .description(dto.getDescription())
                .resourceType(dto.getResourceType().toUpperCase())
                .actionType(dto.getActionType().toUpperCase())
                .build();

        return permissionMapper.toDto(permissionRepository.save(permission));
    }

    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream().map(permissionMapper::toDto).toList();
    }

    public PermissionDTO getPermissionByCode(String code) {
        Permission pr = permissionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + code));

        return permissionMapper.toDto(pr);
    }

    // Gestion des rôles
    public RoleDTO createRole(RoleDTO dto) {
        Role role = Role.builder()
                .name(dto.getName().toUpperCase())
                .description(dto.getDescription())
                .permissions(new HashSet<>())
                .build();

        if (dto.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(
                    permissionRepository.findAllById(dto.getPermissionIds())
            );
            role.setPermissions(permissions);
        }

        return roleMapper.toDto(roleRepository.save(role));
    }

    public RoleDTO updateRolePermissions(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllById(permissionIds)
        );

        role.setPermissions(permissions);
        return roleMapper.toDto(roleRepository.save(role));
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toDto).toList();
    }

    public RoleDTO getRoleWithPermissions(Long roleId) {
        Role rl= roleRepository.findByIdWithPermissions(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return roleMapper.toDto(rl);
    }
}
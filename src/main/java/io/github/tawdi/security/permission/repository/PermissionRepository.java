package io.github.tawdi.security.permission.repository;


import io.github.tawdi.security.permission.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    List<Permission> findByResourceType(String resourceType);

    List<Permission> findByActionType(String actionType);

    @Query("SELECT p FROM Permission p WHERE p.code LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Permission> searchPermissions(@Param("keyword") String keyword);
}
package io.github.tawdi.security.permission.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RoleDTO {

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    @Size(max = 200)
    private String description;

    private Set<Long> permissionIds;
}
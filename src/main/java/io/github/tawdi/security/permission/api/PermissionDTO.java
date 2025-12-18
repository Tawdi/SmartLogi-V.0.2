package io.github.tawdi.security.permission.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionDTO {

    @NotBlank
    @Size(min = 3, max = 100)
    private String code;

    @Size(max = 200)
    private String description;

    @NotBlank
    @Size(min = 3, max = 50)
    private String resourceType;

    @NotBlank
    @Size(min = 3, max = 20)
    private String actionType;
}

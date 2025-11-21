package com.smartlogi.smartlogidms.masterdata.client.api;

import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterClientRequestDTO extends @Valid ClientRequestDTO {

    @NotBlank(groups = ValidationGroups.Create.class, message = "Le nom d'utilisateur est obligatoire")
    @Size(groups = ValidationGroups.Create.class, min = 4, max = 30, message = "Le nom d'utilisateur doit faire entre 4 et 30 caractères")
    private String username;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Le mot de passe est obligatoire")
    @Size(groups = ValidationGroups.Create.class, min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
}

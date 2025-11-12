package com.smartlogi.smartlogidms.masterdata.client.api;

import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import com.smartlogi.smartlogidms.masterdata.shared.api.PersonneRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class ClientRequestDTO extends PersonneRequestDTO {


    @Size(groups = {ValidationGroups.Create.class ,ValidationGroups.Update.class},max = 255, message = "La rue ne doit pas dépasser 255 caractères")
    private String rue;

    @NotBlank(groups = ValidationGroups.Create.class, message = "La ville est obligatoire")
    @Size(groups = {ValidationGroups.Create.class ,ValidationGroups.Update.class},max = 100, message = "La ville ne doit pas dépasser 100 caractères")
    private String ville;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Le code postal est obligatoire")
    @Size(groups = {ValidationGroups.Create.class ,ValidationGroups.Update.class},max = 20, message = "Le code postal ne doit pas dépasser 20 caractères")
    private String codePostal;
}

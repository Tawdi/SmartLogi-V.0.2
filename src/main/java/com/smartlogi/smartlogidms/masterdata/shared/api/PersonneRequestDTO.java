package com.smartlogi.smartlogidms.masterdata.shared.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResquestDTO;
import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class PersonneRequestDTO implements BaseResquestDTO {

    @NotBlank(groups = ValidationGroups.Create.class, message = "Le nom est obligatoire")
    @Size(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    protected String lastName;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Le prénom est obligatoire")
    @Size(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    protected String firstName;


    @Email(groups = ValidationGroups.Create.class, message = "Format de l'email invalide")
    @Size(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    protected String email;

    @NotBlank(groups = ValidationGroups.Create.class, message = "Le numéro de téléphone est obligatoire")
    @Size(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, max = 30, message = "Le numéro de téléphone ne doit pas dépasser 30 caractères")
    protected String phoneNumber;


}

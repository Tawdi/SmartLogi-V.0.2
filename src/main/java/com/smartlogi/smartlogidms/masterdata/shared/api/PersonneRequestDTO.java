package com.smartlogi.smartlogidms.masterdata.shared.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonneRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    protected String lastName;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    protected String firstName;


    @Email(message = "Format de l'email invalide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    protected String email;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Size(max = 30, message = "Le numéro de téléphone ne doit pas dépasser 30 caractères")
    protected String phoneNumber;


}

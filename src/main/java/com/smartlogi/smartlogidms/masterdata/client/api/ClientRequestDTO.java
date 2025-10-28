package com.smartlogi.smartlogidms.masterdata.client.api;

import com.smartlogi.smartlogidms.masterdata.shared.api.PersonneRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientRequestDTO extends PersonneRequestDTO {


    @Size(max = 255, message = "La rue ne doit pas dépasser 255 caractères")
    private String rue;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères")
    private String ville;

    @NotBlank(message = "Le code postal est obligatoire")
    @Size(max = 20, message = "Le code postal ne doit pas dépasser 20 caractères")
    private String codePostal;
}

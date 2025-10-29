package com.smartlogi.smartlogidms.masterdata.zone.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ZoneRequestDTO {

    @NotBlank(message = "Le nom de zone est obligatoire")
    @Size(max = 50, message = "Le nom de zone ne doit pas dépasser 50 caractères")
    private String name;

    @NotBlank(message = "Le code Postal de zone est obligatoire")
    @Size(max = 20, message = "Le code Postal de zone ne doit pas dépasser 20 caractères")
    private String codePostal;
}

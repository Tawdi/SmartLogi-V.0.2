package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResquestDTO;
import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ColisRequestDTO implements BaseResquestDTO {

    @NotBlank(groups = ValidationGroups.Create.class)
    @Size(max = 50)
    private String reference;

    @NotNull(groups = ValidationGroups.Create.class)
    @Positive
    private Double poids;

    @Size(max = 255)
    private String description;

    @NotNull(groups = ValidationGroups.Create.class)
    private Colis.Priorite priorite = Colis.Priorite.MEDIUM;

    @NotNull(groups = ValidationGroups.Create.class)
    private String expediteurId;

    @NotNull(groups = ValidationGroups.Create.class)
    private String destinataireId;

    @NotNull(groups = ValidationGroups.Create.class)
    private String zoneId;

    //    Adresse
    @NotBlank(groups = ValidationGroups.Create.class)
    @Size(max = 100)
    private String ville;
    @Size(max = 255)
    private String rue;
    @NotBlank(groups = ValidationGroups.Create.class)
    @Size(max = 20)
    private String codePostal;
}

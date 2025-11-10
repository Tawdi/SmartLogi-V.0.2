package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResquestDTO;
import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.product.api.ProductRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private Colis.Priorite priorite ;

    @NotNull(groups = ValidationGroups.Create.class)
    private String expediteurId;

    @NotNull(groups = ValidationGroups.Create.class)
    private String destinataireId;

    @NotNull(groups = ValidationGroups.Create.class)
    private String zoneId;

    private List<ProduitColisDTO> productList = new ArrayList<>();

    //    Adresse
    @NotBlank(groups = ValidationGroups.Create.class)
    @Size(max = 100)
    private String ville;
    @Size(max = 255)
    private String rue;
    @NotBlank(groups = ValidationGroups.Create.class)
    @Size(max = 20)
    private String codePostal;

    @Getter @Setter
    @AllArgsConstructor
    public static class ProduitColisDTO {
        private String productId;
        private Integer quantite = 1;
        private Double prix;
    }
}

package com.smartlogi.smartlogidms.delivery.product.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResquestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ProductRequestDTO implements BaseResquestDTO {
    @NotBlank
    private String nom;
    @NotBlank
    private String categorie;
    @NotBlank
    private Double poids;
    @NotBlank
    private Double prix;
}

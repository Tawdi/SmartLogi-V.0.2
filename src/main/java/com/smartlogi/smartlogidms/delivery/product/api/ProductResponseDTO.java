package com.smartlogi.smartlogidms.delivery.product.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDTO extends BaseResponseDTO<String> {
    private String nom;
    private String categorie;
    private Double poids;
    private Double prix;
}
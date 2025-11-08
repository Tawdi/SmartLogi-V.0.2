package com.smartlogi.smartlogidms.delivery.colis.api;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColisProductResponseDTO {
    private String productId;
    private String nom;
    private String categorie;
    private Double poids;
    private Integer quantite;
    private Double prixUnitaire;
    private Double prixTotal;
}

package com.smartlogi.smartlogidms.delivery.colis.domain;

import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "colis_produit")
@Getter
@Setter
@IdClass(ColisProduitId.class)
public class ColisProduit {

    @Id
    @Column(name = "colis_id")
    private String colisId;

    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(nullable = false)
    private Integer quantite = 1;

    @Column(name = "prix_unitaire")
    private Double prixUnitaire;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout = LocalDateTime.now();

    // Relations (lecture seule)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "colis_id", insertable = false, updatable = false)
    private Colis colis;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
}
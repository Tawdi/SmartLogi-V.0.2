package com.smartlogi.smartlogidms.delivery.product.domain;

import com.smartlogi.smartlogidms.common.domain.entity.Id.LongBaseEntity;
import com.smartlogi.smartlogidms.common.domain.entity.Id.StringBaseEntity;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends StringBaseEntity {


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "colis_id",nullable = false)
    @NotNull
    private Colis colis;
    private String nom;
    private String  categorie;
    private Double poids;
    private Double prix;
}

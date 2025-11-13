package com.smartlogi.smartlogidms.delivery.product.domain;

import com.smartlogi.smartlogidms.common.annotation.Searchable;
import com.smartlogi.smartlogidms.common.domain.entity.id.StringBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@Searchable(fields={"nom","categorie"})
public class Product extends StringBaseEntity {



    private String nom;
    private String  categorie;
    private Double poids;
    private Double prix;
}

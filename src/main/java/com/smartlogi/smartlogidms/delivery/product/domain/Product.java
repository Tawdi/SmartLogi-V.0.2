package com.smartlogi.smartlogidms.delivery.product.domain;

import com.smartlogi.smartlogidms.common.annotation.Searchable;
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
@Searchable(fields={"nom","categorie"})
public class Product extends StringBaseEntity {



    private String nom;
    private String  categorie;
    private Double poids;
    private Double prix;
}

package com.smartlogi.smartlogidms.delivery.colis.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ColisProduitId implements Serializable {
    private String colisId;
    private String productId;
}
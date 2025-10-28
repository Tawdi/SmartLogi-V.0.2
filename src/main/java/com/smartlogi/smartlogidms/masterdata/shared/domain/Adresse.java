package com.smartlogi.smartlogidms.masterdata.shared.domain;

import com.smartlogi.smartlogidms.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Adresse extends BaseEntity {

    private String ville;
    private String rue;
    private String codePostal;
}

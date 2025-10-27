package com.smartlogi.smartlogidms.masterdata.shared.domain;

import com.smartlogi.smartlogidms.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Adresse extends BaseEntity {

    private String ville;
    private String rue;
    private String codePostal;
}

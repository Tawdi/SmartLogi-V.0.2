package com.smartlogi.smartlogidms.masterdata.shared.domain;


import com.smartlogi.smartlogidms.common.domain.StringBaseEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Entity // ?
@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Adresse {

    private String ville;
    private String rue;
    private String codePostal;
}

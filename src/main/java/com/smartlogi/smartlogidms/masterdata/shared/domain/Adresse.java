package com.smartlogi.smartlogidms.masterdata.shared.domain;


import com.smartlogi.smartlogidms.common.domain.StringBaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Adresse extends StringBaseEntity {

    private String ville;
    private String rue;
    private String codePostal;
}

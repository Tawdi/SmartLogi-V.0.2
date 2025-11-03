package com.smartlogi.smartlogidms.masterdata.shared.domain;


import jakarta.persistence.Embeddable;
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

package com.smartlogi.smartlogidms.masterdata.recipient.domain;

import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recipient")
@Getter
@Setter
public class Recipient extends Personne {


    @Embedded
    private Adresse adresse;

    public Recipient(String firstName, String lastName, String email, String phoneNumber, Adresse adresse) {
        super(firstName, lastName, email, phoneNumber, PersonneRole.CLIENT);
        this.adresse = adresse;
    }

    public Recipient(){
        this.setRole(PersonneRole.CLIENT);
    }



}

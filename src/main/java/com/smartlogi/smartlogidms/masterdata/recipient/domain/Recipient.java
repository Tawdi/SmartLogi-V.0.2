package com.smartlogi.smartlogidms.masterdata.recipient.domain;

import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "recipient")
@Getter
@Setter
public class Recipient extends Personne {


    @OneToOne
    @JoinColumn(name = "adresse_id")
    private Adresse adresse;

    public Recipient(String firstName, String lastName, String email, String phoneNumber, Adresse adresse) {
        super(firstName, lastName, email, phoneNumber, PersonneRole.CLIENT);
        this.adresse = adresse;
    }

    public Recipient(){
        this.setRole(PersonneRole.CLIENT);
    }



}

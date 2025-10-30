package com.smartlogi.smartlogidms.masterdata.client.domain;

import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "senders")
@Setter
@Getter
public final class ClientExpediteur extends Personne {



    @Embedded
    private Adresse adresse;

    public ClientExpediteur(){
        this.setRole(PersonneRole.CLIENT);
    }


    public ClientExpediteur(String firstName, String lastName, String email, String phoneNumber, Adresse adresse) {
        super(firstName, lastName, email, phoneNumber, PersonneRole.CLIENT);
        this.adresse = adresse;
    }
}

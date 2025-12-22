package com.smartlogi.smartlogidms.masterdata.client.domain;

import com.smartlogi.smartlogidms.common.annotation.Searchable;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne;
import io.github.tawdi.security.user.domain.UserAccount;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "senders")
@Setter
@Getter
@Searchable(fields = {"firstName", "lastName", "phoneNumber", "email"})
public final class ClientExpediteur extends Personne {


    @Embedded
    private Adresse adresse;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_account_id", unique = true)
    private UserAccount userAccount;

    public ClientExpediteur() {
    }


    public ClientExpediteur(String firstName, String lastName, String email, String phoneNumber, Adresse adresse) {
        super(firstName, lastName, email, phoneNumber);
        this.adresse = adresse;
    }
}

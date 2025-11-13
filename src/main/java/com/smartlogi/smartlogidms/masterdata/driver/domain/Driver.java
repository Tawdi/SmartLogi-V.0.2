package com.smartlogi.smartlogidms.masterdata.driver.domain;

import com.smartlogi.smartlogidms.common.annotation.Searchable;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "livreur")
@Searchable(fields = {"firstName", "lastName", "phoneNumber" })
public class Driver extends Personne {

    @Column(length = 100)
    private String vehicule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zone_assignee_id", referencedColumnName = "id")
    private Zone zoneAssignee;

    public Driver() {
        this.setRole(PersonneRole.DRIVER);
    }


    public Driver(String firstName, String lastName, String email, String phoneNumber, String vehicule) {
        super(firstName, lastName, email, phoneNumber, PersonneRole.DRIVER);
        this.vehicule = vehicule;
    }
}

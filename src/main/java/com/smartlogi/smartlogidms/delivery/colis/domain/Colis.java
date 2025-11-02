package com.smartlogi.smartlogidms.delivery.colis.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.smartlogi.smartlogidms.common.domain.StringBaseEntity;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "colis")
@Getter
@Setter
@NoArgsConstructor
public class Colis extends StringBaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Column(nullable = false)
    private Double poids;     // kg

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ColisStatus statut = ColisStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priorite priorite = Priorite.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private ClientExpediteur expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Recipient destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livreur_id")
    private Driver livreur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Embedded
    private Adresse adresseLivraison;

    public enum ColisStatus {
        CREATED,
        COLLECTED,
        IN_STOCK,
        IN_TRANSIT,
        DELIVERED;

        @JsonCreator
        public static ColisStatus fromString(String key) {
            return key == null
                    ? null
                    : ColisStatus.valueOf(key.toUpperCase());
        }
    }

    public enum Priorite {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
}

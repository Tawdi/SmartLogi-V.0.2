package com.smartlogi.smartlogidms.delivery.historique.domain;

import com.smartlogi.smartlogidms.common.domain.StringBaseEntity;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_livraison")
@Getter
@Setter
@NoArgsConstructor
public class HistoriqueLivraison extends StringBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;

    @Enumerated(EnumType.STRING)
    @Column(name = "ancien_statut", nullable = false)
    private Colis.ColisStatus ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(name = "nouveau_statut", nullable = false)
    private Colis.ColisStatus nouveauStatut;

    @Column(name = "date_changement", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime dateChangement;

    @Column(name = "utilisateur_id", nullable = false)
    private String utilisateurId;

    @Column(name = "commentaire", length = 500)
    private String commentaire;

    public HistoriqueLivraison(Colis colis, Colis.ColisStatus ancienStatut,
                               Colis.ColisStatus nouveauStatut, String utilisateurId, String commentaire) {
        this.colis = colis;
        this.ancienStatut = ancienStatut;
        this.nouveauStatut = nouveauStatut;
        this.utilisateurId = utilisateurId;
        this.commentaire = commentaire;
    }
}
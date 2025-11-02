package com.smartlogi.smartlogidms.delivery.historique.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResponseDTO;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HistoriqueLivraisonResponseDTO extends BaseResponseDTO<String> {
    private String colisId;
    private String colisReference;
    private Colis.ColisStatus ancienStatut;
    private Colis.ColisStatus nouveauStatut;
    private LocalDateTime dateChangement;
    private String utilisateurId;
    private String commentaire;
}
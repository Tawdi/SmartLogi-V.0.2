package com.smartlogi.smartlogidms.delivery.colis.service;

import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.delivery.colis.api.*;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ColisService extends StringCrudService<ColisRequestDTO, ColisResponseDTO> {

    Page<ColisResponseDTO> findByExpediteurId(String expediteurId, Colis.ColisStatus status, Pageable pageable);

    Page<ColisResponseDTO> findByDestinataireId(String destinataireId, Colis.ColisStatus status, Pageable pageable);

    Page<ColisResponseDTO> findByLivreurId(String livreurId, Colis.ColisStatus status, Pageable pageable);

    ColisResponseDTO updateStatus(String id, UpdateStatusRequest newStatus);

    // history
    Page<HistoriqueLivraisonResponseDTO> getHistory(String colisId, Pageable pageable);

    // assigner Livreur
    ColisResponseDTO assignerLivreur(String colisId, AssignerLivreurRequestDTO request);


    // Synthese
    List<SyntheseDTO<String>> getSyntheseByZone();
    List<SyntheseDTO<Colis.ColisStatus>> getSyntheseByStatut();
    List<SyntheseDTO<Colis.Priorite>> getSyntheseByPriorite();
}

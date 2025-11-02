package com.smartlogi.smartlogidms.delivery.colis.service;

import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.delivery.colis.api.ColisRequestDTO;
import com.smartlogi.smartlogidms.delivery.colis.api.ColisResponseDTO;
import com.smartlogi.smartlogidms.delivery.colis.api.UpdateStatusRequest;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColisService extends StringCrudService<ColisRequestDTO, ColisResponseDTO> {

    Page<ColisResponseDTO> findByExpediteurId(String expediteurId, Colis.ColisStatus status, Pageable pageable);
    Page<ColisResponseDTO> findByDestinataireId(String destinataireId, Colis.ColisStatus status, Pageable pageable);
    ColisResponseDTO updateStatus(String id, UpdateStatusRequest newStatus);

    // history
    Page<HistoriqueLivraisonResponseDTO> getHistory(String colisId, Pageable pageable);
}

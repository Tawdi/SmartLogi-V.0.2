package com.smartlogi.smartlogidms.delivery.colis.service;

import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.delivery.colis.api.ColisRequestDTO;
import com.smartlogi.smartlogidms.delivery.colis.api.ColisResponseDTO;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ColisService extends StringCrudService<ColisRequestDTO, ColisResponseDTO> {
    Page<ColisResponseDTO> findByExpediteurId(String expediteurId, Colis.ColisStatus status, Pageable pageable);
}

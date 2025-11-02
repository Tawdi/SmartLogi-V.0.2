package com.smartlogi.smartlogidms.delivery.historique.domain;

import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriqueLivraisonRepository extends StringRepository<HistoriqueLivraison> {
    Page<HistoriqueLivraison> findByColisId(String colisId, Pageable pageable);
}
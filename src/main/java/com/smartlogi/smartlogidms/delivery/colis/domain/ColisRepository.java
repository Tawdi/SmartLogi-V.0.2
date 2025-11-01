package com.smartlogi.smartlogidms.delivery.colis.domain;

import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ColisRepository extends StringRepository<Colis> {
    Page<Colis> findByStatut(Colis.ColisStatus statut, Pageable pageable);
}

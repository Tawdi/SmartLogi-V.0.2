package com.smartlogi.smartlogidms.delivery.colis.domain;

import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColisRepository extends StringRepository<Colis> {
    Page<Colis> findByStatut(Colis.ColisStatus statut, Pageable pageable);

    @Query("SELECT c FROM Colis c WHERE c.expediteur.id = :expediteurId AND (:statut IS NULL OR c.statut = :statut)")
    Page<Colis> findByExpediteurId(
            @Param("expediteurId") String expediteurId,
            @Param("statut") Colis.ColisStatus statut,
            Pageable pageable);

    @Query("SELECT c FROM Colis c WHERE c.expediteur.id = :expediteurId AND c.statut IN :statuts")
    Page<Colis> findByExpediteurIdAndStatuts(
            @Param("expediteurId") String expediteurId,
            @Param("statuts") List<Colis.ColisStatus> statuts,
            Pageable pageable);
}

package com.smartlogi.smartlogidms.delivery.colis.domain;

import com.smartlogi.smartlogidms.common.domain.repository.StringRepository;
import com.smartlogi.smartlogidms.delivery.colis.api.SyntheseDTO;
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

    @Query("SELECT c FROM Colis c WHERE c.destinataire.id = :destinataireId and (:statut IS NULL OR c.statut = :statut ) ")
    Page<Colis> findByDestinataireId(
            @Param("destinataireId") String destinataireId,
            @Param("statut") Colis.ColisStatus statut,
            Pageable pageable);

    @Query("SELECT c FROM Colis c WHERE c.livreur.id = :livreurId and (:statut IS NULL OR c.statut = :statut ) ")
    Page<Colis> findByLivreurId(
            @Param("livreurId") String livreurId,
            @Param("statut") Colis.ColisStatus statut,
            Pageable pageable);


    @Query("SELECT new com.smartlogi.smartlogidms.delivery.colis.api.SyntheseDTO(z.name, COUNT(c), SUM(c.poids)) " +
            "FROM Colis c JOIN c.zone z  GROUP BY z.id, z.name")
    List<SyntheseDTO<String>> countByZone();

    @Query("SELECT new com.smartlogi.smartlogidms.delivery.colis.api.SyntheseDTO(c.statut, COUNT(c), SUM(c.poids)) " +
            "FROM Colis c GROUP BY c.statut")
    List<SyntheseDTO<Colis.ColisStatus>> countByStatut();

    @Query("SELECT new com.smartlogi.smartlogidms.delivery.colis.api.SyntheseDTO(c.priorite, COUNT(c), SUM(c.poids)) " +
            "FROM Colis c GROUP BY c.priorite")
    List<SyntheseDTO<Colis.Priorite>> countByPriorite();


    @Query("SELECT cp FROM ColisProduit cp WHERE cp.colisId = :colisId")
    Page<ColisProduit> findColisProduitsByColisId(@Param("colisId") String colisId, Pageable pageable);

}

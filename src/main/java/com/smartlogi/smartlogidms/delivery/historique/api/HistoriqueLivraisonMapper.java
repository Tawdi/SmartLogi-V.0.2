package com.smartlogi.smartlogidms.delivery.historique.api;

import com.smartlogi.smartlogidms.delivery.historique.domain.HistoriqueLivraison;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoriqueLivraisonMapper {

    @Mapping(target = "colisId", source = "colis.id")
    @Mapping(target = "colisReference", source = "colis.reference")
    HistoriqueLivraisonResponseDTO toDto(HistoriqueLivraison entity);

}
package com.smartlogi.smartlogidms.delivery.historique.api;

import com.smartlogi.smartlogidms.delivery.historique.domain.HistoriqueLivraison;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface HistoriqueLivraisonMapper {

    HistoriqueLivraisonResponseDTO toDto(HistoriqueLivraison entity);

}
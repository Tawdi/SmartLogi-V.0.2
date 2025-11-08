package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientMapper;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientMapper;
import com.smartlogi.smartlogidms.masterdata.shared.api.AdresseMapper;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ClientMapper.class, RecipientMapper.class, ZoneMapper.class, AdresseMapper.class,})
public interface ColisMapper extends BaseMapper<Colis, ColisRequestDTO, ColisResponseDTO> {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reference", source = "reference")
    @Mapping(target = "poids", source = "poids")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "priorite", source = "priorite")
    @Mapping(target = "statut", constant = "CREATED")
    @Mapping(target = "expediteur", ignore = true)
    @Mapping(target = "destinataire", ignore = true)
    @Mapping(target = "zone", ignore = true)
    @Mapping(target = "livreur", ignore = true)
    @Mapping(target = "adresseLivraison.ville", source = "ville")
    @Mapping(target = "adresseLivraison.rue", source = "rue")
    @Mapping(target = "adresseLivraison.codePostal", source = "codePostal")
    Colis toEntity(ColisRequestDTO requestDTO);


    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "livreur", ignore = true)
    @Mapping(target = "expediteur", ignore = true)
    @Mapping(target = "destinataire", ignore = true)
    @Mapping(target = "zone", ignore = true)
    @Mapping(target = "adresseLivraison.ville", source = "ville")
    @Mapping(target = "adresseLivraison.rue", source = "rue")
    @Mapping(target = "adresseLivraison.codePostal", source = "codePostal")
    void updateEntityFromDto(ColisRequestDTO dto, @MappingTarget Colis entity);


    @Mapping(target = "expediteur", source = "expediteur")
    @Mapping(target = "destinataire", source = "destinataire")
    @Mapping(target = "zone", source = "zone")
    @Mapping(target = "livreur", source = "livreur")
    @Mapping(target = "adresseLivraison", source = "adresseLivraison")
    ColisResponseDTO toDto(Colis entity);
}
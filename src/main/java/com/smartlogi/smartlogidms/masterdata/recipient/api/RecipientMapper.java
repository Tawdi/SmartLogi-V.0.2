package com.smartlogi.smartlogidms.masterdata.recipient.api;

import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RecipientMapper extends BaseMapper<Recipient,RecipientRequestDTO,RecipientResponseDTO> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "adresse.rue", source = "rue")
    @Mapping(target = "adresse.ville", source = "ville")
    @Mapping(target = "adresse.codePostal", source = "codePostal")
    Recipient requestDtoToEntity(RecipientRequestDTO requestDTO);

    @Override
    @Mapping(target = "rue", source = "adresse.rue")
    @Mapping(target = "ville", source = "adresse.ville")
    @Mapping(target = "codePostal", source = "adresse.codePostal")
    RecipientResponseDTO entityToResponseDto(Recipient entity);
}

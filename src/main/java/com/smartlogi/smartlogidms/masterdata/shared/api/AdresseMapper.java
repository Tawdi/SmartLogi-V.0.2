package com.smartlogi.smartlogidms.masterdata.shared.api;

import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdresseMapper extends BaseMapper<Adresse, AdresseDTO, AdresseDTO> {
    @Override
    @Mapping(target = "ville", source = "ville")
    @Mapping(target = "rue", source = "rue")
    @Mapping(target = "codePostal", source = "codePostal")
    Adresse toEntity(AdresseDTO dto);

    @Override
    @Mapping(target = "ville", source = "ville")
    @Mapping(target = "rue", source = "rue")
    @Mapping(target = "codePostal", source = "codePostal")
    void updateEntityFromDto(AdresseDTO dto, @MappingTarget Adresse entity);
}

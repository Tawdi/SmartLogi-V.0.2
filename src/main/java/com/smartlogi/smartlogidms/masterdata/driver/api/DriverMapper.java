package com.smartlogi.smartlogidms.masterdata.driver.api;

import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;


@Mapper
public interface DriverMapper extends BaseMapper<Driver, DriverRequestDTO, DriverResponseDTO> {

    @Override
    @Mapping(source = "zoneAssignee.id", target = "zoneAssigneeId")
    @Mapping(source = "zoneAssignee.name", target = "zoneAssigneeNom")
    DriverResponseDTO toDto(Driver entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "zoneAssignee", ignore = true)
    Driver toEntity(DriverRequestDTO requestDto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "zoneAssignee", ignore = true)
    void updateEntityFromDto(DriverRequestDTO dto, @MappingTarget Driver entity);

}

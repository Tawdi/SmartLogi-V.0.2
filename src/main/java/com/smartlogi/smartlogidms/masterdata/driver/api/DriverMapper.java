package com.smartlogi.smartlogidms.masterdata.driver.api;

import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DriverMapper extends BaseMapper<Driver, DriverRequestDTO, DriverResponseDTO> {

    @Override
    @Mapping(source = "zoneAssignee.id", target = "zoneAssigneeId")
    @Mapping(source = "zoneAssignee.name", target = "zoneAssigneeNom")
    DriverResponseDTO entityToResponseDto(Driver entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "zoneAssignee", ignore = true)
    Driver requestDtoToEntity(DriverRequestDTO requestDto);
}

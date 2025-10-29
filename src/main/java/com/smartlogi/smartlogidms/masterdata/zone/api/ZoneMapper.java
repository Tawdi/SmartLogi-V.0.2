package com.smartlogi.smartlogidms.masterdata.zone.api;

import com.smartlogi.smartlogidms.common.mapper.BaseMapper;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ZoneMapper extends BaseMapper<Zone, ZoneRequestDTO, ZoneResponseDTO> {
}

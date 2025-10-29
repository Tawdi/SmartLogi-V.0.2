package com.smartlogi.smartlogidms.masterdata.zone.service;

import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneMapper;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneRequestDTO;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneResponseDTO;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;

public class ZoneServiceImpl extends StringCrudServiceImpl<Zone, ZoneRequestDTO, ZoneResponseDTO> implements ZoneService {

    private final ZoneRepository repository;
    private final ZoneMapper mapper;

    public ZoneServiceImpl(ZoneRepository zoneRepository, ZoneMapper zoneMapper) {
        super(zoneRepository, zoneMapper);
        this.repository = zoneRepository;
        this.mapper = zoneMapper;
    }
}

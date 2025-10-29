package com.smartlogi.smartlogidms.masterdata.driver.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverMapper;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverRequestDTO;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverResponseDTO;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import com.smartlogi.smartlogidms.masterdata.driver.domain.DriverRepository;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DriverServiceImpl extends StringCrudServiceImpl<Driver, DriverRequestDTO, DriverResponseDTO> implements DriverService {

    private final DriverRepository repository;
    private final ZoneRepository zoneRepository;
    private final DriverMapper mapper;

    public DriverServiceImpl(DriverRepository driverRepository, ZoneRepository zoneRepo, DriverMapper driverMapper) {
        super(driverRepository, driverMapper);
        this.repository = driverRepository;
        this.mapper = driverMapper;
        this.zoneRepository = zoneRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DriverResponseDTO> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(mapper::entityToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponseDTO> searchDrivers(String keyword, Pageable pageable) {
        return repository.searchDrivers(keyword, pageable)
                .map(mapper::entityToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponseDTO> findDriversByZone(String zoneId, Pageable pageable) {
        return repository.findByZoneAssigneeId(zoneId, pageable)
                .map(mapper::entityToResponseDto);
    }

    @Override
    @Transactional
    public DriverResponseDTO assignZoneToDriver(String driverId, String zoneId) {

        Driver driver = repository.findById(driverId).orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + driverId));

        Zone zone = zoneRepository.findById(zoneId).orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        driver.setZoneAssignee(zone);

        Driver updatedDriver = repository.save(driver);

        return mapper.entityToResponseDto(updatedDriver);
    }
}

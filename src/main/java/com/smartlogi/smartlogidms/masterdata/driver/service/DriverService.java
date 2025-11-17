package com.smartlogi.smartlogidms.masterdata.driver.service;

import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverRequestDTO;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverResponseDTO;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DriverService extends StringCrudService<Driver, DriverRequestDTO, DriverResponseDTO> {

    Optional<DriverResponseDTO> findByEmail(String email);

    Page<DriverResponseDTO> searchDrivers(String keyword, Pageable pageable);

    Page<DriverResponseDTO> findDriversByZone(String zoneId, Pageable pageable);

    DriverResponseDTO assignZoneToDriver(String driverId, String zoneId);
}

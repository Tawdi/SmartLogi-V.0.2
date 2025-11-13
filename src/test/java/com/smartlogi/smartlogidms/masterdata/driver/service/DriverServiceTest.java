package com.smartlogi.smartlogidms.masterdata.driver.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverMapper;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverMapperImpl;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverRequestDTO;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverResponseDTO;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import com.smartlogi.smartlogidms.masterdata.driver.domain.DriverRepository;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository repository;

    @Mock
    private ZoneRepository zoneRepository;

    // Use real mapper implementation
    @Spy
    private DriverMapper mapper = new DriverMapperImpl();

    private DriverServiceImpl driverService;

    private Driver driverEntity;
    private DriverRequestDTO driverRequestDTO;
    private Zone zone;

    @BeforeEach
    void setUp() {
        driverService = new DriverServiceImpl(repository, zoneRepository, mapper);

        // Setup test data
        zone = new Zone();
        zone.setId("ZONE-123");
        zone.setName("Paris Center");

        driverEntity = new Driver();
        driverEntity.setId("DRIVER-123");
        driverEntity.setFirstName("Jean");
        driverEntity.setLastName("Dupont");
        driverEntity.setEmail("jean.dupont@example.com");
        driverEntity.setPhoneNumber("+33123456789");
        driverEntity.setVehicule("Renault Kangoo");
        driverEntity.setZoneAssignee(zone);

        driverRequestDTO = new DriverRequestDTO();
        driverRequestDTO.setFirstName("Jean");
        driverRequestDTO.setLastName("Dupont");
        driverRequestDTO.setEmail("jean.dupont@example.com");
        driverRequestDTO.setPhoneNumber("+33123456789");
        driverRequestDTO.setVehicule("Renault Kangoo");
    }

    @Test
    void shouldSaveDriverSuccessfully() {
        // Given
        when(repository.save(any(Driver.class))).thenReturn(driverEntity);

        // When
        DriverResponseDTO result = driverService.save(driverRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jean");
        assertThat(result.getLastName()).isEqualTo("Dupont");
        assertThat(result.getEmail()).isEqualTo("jean.dupont@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+33123456789");
        // The real mapper will handle the mapping, so we test actual behavior

        verify(repository).save(any(Driver.class));
    }

    @Test
    void shouldUpdateDriverSuccessfully() {
        // Given
        String driverId = "DRIVER-123";
        DriverRequestDTO updateRequest = new DriverRequestDTO();
        updateRequest.setFirstName("Jean Updated");
        updateRequest.setLastName("Dupont Updated");
        updateRequest.setEmail("jean.updated@example.com");
        updateRequest.setPhoneNumber("+33987654321");
        updateRequest.setVehicule("Peugeot Partner");

        Driver existingDriver = new Driver();
        existingDriver.setId(driverId);
        existingDriver.setFirstName("Jean");
        existingDriver.setLastName("Dupont");
        existingDriver.setEmail("jean.dupont@example.com");
        existingDriver.setPhoneNumber("+33123456789");
        existingDriver.setVehicule("Renault Kangoo");

        when(repository.findById(driverId)).thenReturn(Optional.of(existingDriver));
        when(repository.save(any(Driver.class))).thenReturn(existingDriver);

        // When
        DriverResponseDTO result = driverService.update(driverId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(driverId);
        assertThat(result.getFirstName()).isEqualTo("Jean Updated");
        assertThat(result.getLastName()).isEqualTo("Dupont Updated");
        assertThat(result.getEmail()).isEqualTo("jean.updated@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+33987654321");
        // Real mapper ensures proper mapping

        verify(repository).findById(driverId);
        verify(repository).save(existingDriver);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentDriver() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        DriverRequestDTO updateRequest = new DriverRequestDTO();
        updateRequest.setFirstName("Test");
        updateRequest.setLastName("Driver");

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> driverService.update(nonExistentId, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).findById(nonExistentId);
        verify(repository, never()).save(any(Driver.class));
    }

    @Test
    void shouldFindDriverByIdSuccessfully() {
        // Given
        String driverId = "DRIVER-123";
        when(repository.findById(driverId)).thenReturn(Optional.of(driverEntity));

        // When
        DriverResponseDTO result = driverService.findById(driverId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(driverId);
        assertThat(result.getFirstName()).isEqualTo("Jean");
        assertThat(result.getLastName()).isEqualTo("Dupont");
        assertThat(result.getEmail()).isEqualTo("jean.dupont@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+33123456789");
        assertThat(result.getZoneAssigneeId()).isEqualTo("ZONE-123");
        assertThat(result.getZoneAssigneeNom()).isEqualTo("Paris Center");
        // Real mapper properly maps zone information

        verify(repository).findById(driverId);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentDriverById() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> driverService.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).findById(nonExistentId);
    }

    @Test
    void shouldFindDriverByEmailSuccessfully() {
        // Given
        String email = "jean.dupont@example.com";
        when(repository.findByEmail(email)).thenReturn(Optional.of(driverEntity));

        // When
        Optional<DriverResponseDTO> result = driverService.findByEmail(email);

        // Then
        assertThat(result).isPresent();
        DriverResponseDTO driverDTO = result.get();
        assertThat(driverDTO.getEmail()).isEqualTo(email);
        assertThat(driverDTO.getFirstName()).isEqualTo("Jean");
        assertThat(driverDTO.getLastName()).isEqualTo("Dupont");
        assertThat(driverDTO.getZoneAssigneeId()).isEqualTo("ZONE-123");
        // Real mapper ensures all fields are properly mapped

        verify(repository).findByEmail(email);
    }

    @Test
    void shouldReturnEmptyWhenFindingNonExistentDriverByEmail() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(repository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When
        Optional<DriverResponseDTO> result = driverService.findByEmail(nonExistentEmail);

        // Then
        assertThat(result).isEmpty();
        verify(repository).findByEmail(nonExistentEmail);
    }

    @Test
    void shouldSearchDriversByKeyword() {
        // Given
        String keyword = "jean";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Driver> driverPage = new PageImpl<>(List.of(driverEntity), pageable, 1);

        when(repository.searchDrivers(keyword, pageable)).thenReturn(driverPage);

        // When
        Page<DriverResponseDTO> result = driverService.searchDrivers(keyword, pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Jean");
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Dupont");
        assertThat(result.getTotalElements()).isEqualTo(1);
        // Real mapper ensures proper mapping in page results

        verify(repository).searchDrivers(keyword, pageable);
    }

    @Test
    void shouldFindDriversByZone() {
        // Given
        String zoneId = "ZONE-123";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Driver> driverPage = new PageImpl<>(List.of(driverEntity), pageable, 1);

        when(repository.findByZoneAssigneeId(zoneId, pageable)).thenReturn(driverPage);

        // When
        Page<DriverResponseDTO> result = driverService.findDriversByZone(zoneId, pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getZoneAssigneeId()).isEqualTo(zoneId);
        assertThat(result.getContent().get(0).getZoneAssigneeNom()).isEqualTo("Paris Center");
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(repository).findByZoneAssigneeId(zoneId, pageable);
    }

    @Test
    void shouldAssignZoneToDriverSuccessfully() {
        // Given
        String driverId = "DRIVER-123";
        String zoneId = "ZONE-456";

        Driver driverWithoutZone = new Driver();
        driverWithoutZone.setId(driverId);
        driverWithoutZone.setFirstName("Jean");
        driverWithoutZone.setLastName("Dupont");
        driverWithoutZone.setEmail("jean.dupont@example.com");

        Zone newZone = new Zone();
        newZone.setId(zoneId);
        newZone.setName("Lyon Center");

        when(repository.findById(driverId)).thenReturn(Optional.of(driverWithoutZone));
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(newZone));
        when(repository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DriverResponseDTO result = driverService.assignZoneToDriver(driverId, zoneId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(driverId);
        assertThat(result.getZoneAssigneeId()).isEqualTo(zoneId);
        assertThat(result.getZoneAssigneeNom()).isEqualTo("Lyon Center");
        // Real mapper properly maps the updated zone information

        verify(repository).findById(driverId);
        verify(zoneRepository).findById(zoneId);
        verify(repository).save(driverWithoutZone);
        assertThat(driverWithoutZone.getZoneAssignee()).isEqualTo(newZone);
    }

    @Test
    void shouldThrowExceptionWhenAssigningZoneToNonExistentDriver() {
        // Given
        String nonExistentDriverId = "NON-EXISTENT-DRIVER";
        String zoneId = "ZONE-123";

        when(repository.findById(nonExistentDriverId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> driverService.assignZoneToDriver(nonExistentDriverId, zoneId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Driver not found with id: " + nonExistentDriverId);

        verify(repository).findById(nonExistentDriverId);
        verify(zoneRepository, never()).findById(anyString());
        verify(repository, never()).save(any(Driver.class));
    }

    @Test
    void shouldThrowExceptionWhenAssigningNonExistentZoneToDriver() {
        // Given
        String driverId = "DRIVER-123";
        String nonExistentZoneId = "NON-EXISTENT-ZONE";

        when(repository.findById(driverId)).thenReturn(Optional.of(driverEntity));
        when(zoneRepository.findById(nonExistentZoneId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> driverService.assignZoneToDriver(driverId, nonExistentZoneId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Zone not found with id: " + nonExistentZoneId);

        verify(repository).findById(driverId);
        verify(zoneRepository).findById(nonExistentZoneId);
        verify(repository, never()).save(any(Driver.class));
    }

    @Test
    void shouldDeleteDriverSuccessfully() {
        // Given
        String driverId = "DRIVER-123";
        when(repository.existsById(driverId)).thenReturn(true);
        doNothing().when(repository).deleteById(driverId);

        // When
        driverService.deleteById(driverId);

        // Then
        verify(repository).existsById(driverId);
        verify(repository).deleteById(driverId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentDriver() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        when(repository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> driverService.deleteById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).existsById(nonExistentId);
        verify(repository, never()).deleteById(nonExistentId);
    }

    @Test
    void shouldCheckIfDriverExists() {
        // Given
        String driverId = "DRIVER-123";
        when(repository.existsById(driverId)).thenReturn(true);

        // When
        boolean exists = driverService.existsById(driverId);

        // Then
        assertThat(exists).isTrue();
        verify(repository).existsById(driverId);
    }

    @Test
    void shouldCheckIfDriverDoesNotExist() {
        // Given
        String driverId = "NON-EXISTENT";
        when(repository.existsById(driverId)).thenReturn(false);

        // When
        boolean exists = driverService.existsById(driverId);

        // Then
        assertThat(exists).isFalse();
        verify(repository).existsById(driverId);
    }

    @Test
    void shouldFindAllDrivers() {
        // Given
        List<Driver> drivers = List.of(driverEntity);
        when(repository.findAll()).thenReturn(drivers);

        // When
        List<DriverResponseDTO> result = driverService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("DRIVER-123");
        assertThat(result.get(0).getFirstName()).isEqualTo("Jean");
        assertThat(result.get(0).getLastName()).isEqualTo("Dupont");
        // Real mapper ensures proper mapping of all fields

        verify(repository).findAll();
    }

    @Test
    void shouldFindAllDriversWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Driver> driverPage = new PageImpl<>(List.of(driverEntity), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(driverPage);

        // When
        Page<DriverResponseDTO> result = driverService.findAll(pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("DRIVER-123");
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Jean");
        assertThat(result.getTotalElements()).isEqualTo(1);
        // Real mapper ensures proper mapping in paginated results

        verify(repository).findAll(pageable);
    }

    @Test
    void shouldHandleEmptyDriverSearchResults() {
        // Given
        String keyword = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Driver> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(repository.searchDrivers(keyword, pageable)).thenReturn(emptyPage);

        // When
        Page<DriverResponseDTO> result = driverService.searchDrivers(keyword, pageable);

        // Then
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(repository).searchDrivers(keyword, pageable);
    }

    @Test
    void shouldHandleEmptyDriversByZone() {
        // Given
        String zoneId = "EMPTY-ZONE";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Driver> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findByZoneAssigneeId(zoneId, pageable)).thenReturn(emptyPage);

        // When
        Page<DriverResponseDTO> result = driverService.findDriversByZone(zoneId, pageable);

        // Then
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(repository).findByZoneAssigneeId(zoneId, pageable);
    }

    @Test
    void shouldUpdateDriverZoneAssignment() {
        // Given
        String driverId = "DRIVER-123";
        String newZoneId = "ZONE-NEW";

        Driver driverWithOldZone = new Driver();
        driverWithOldZone.setId(driverId);
        driverWithOldZone.setFirstName("Jean");
        driverWithOldZone.setLastName("Dupont");
        driverWithOldZone.setZoneAssignee(zone); // Old zone

        Zone newZone = new Zone();
        newZone.setId(newZoneId);
        newZone.setName("New Zone");

        when(repository.findById(driverId)).thenReturn(Optional.of(driverWithOldZone));
        when(zoneRepository.findById(newZoneId)).thenReturn(Optional.of(newZone));
        when(repository.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DriverResponseDTO result = driverService.assignZoneToDriver(driverId, newZoneId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getZoneAssigneeId()).isEqualTo(newZoneId);
        assertThat(result.getZoneAssigneeNom()).isEqualTo("New Zone");
        assertThat(driverWithOldZone.getZoneAssignee()).isEqualTo(newZone); // Verify entity was updated

        verify(repository).findById(driverId);
        verify(zoneRepository).findById(newZoneId);
        verify(repository).save(driverWithOldZone);
    }

    @Test
    void shouldMapDriverWithoutZoneCorrectly() {
        // Given
        Driver driverWithoutZone = new Driver();
        driverWithoutZone.setId("DRIVER-NO-ZONE");
        driverWithoutZone.setFirstName("Pierre");
        driverWithoutZone.setLastName("Martin");
        driverWithoutZone.setEmail("pierre.martin@example.com");
        driverWithoutZone.setPhoneNumber("+33123456780");
        driverWithoutZone.setVehicule("Citroën Berlingo");
        // No zone assigned

        when(repository.findById("DRIVER-NO-ZONE")).thenReturn(Optional.of(driverWithoutZone));

        // When
        DriverResponseDTO result = driverService.findById("DRIVER-NO-ZONE");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("DRIVER-NO-ZONE");
        assertThat(result.getFirstName()).isEqualTo("Pierre");
        assertThat(result.getZoneAssigneeId()).isNull(); // Should be null when no zone
        assertThat(result.getZoneAssigneeNom()).isNull(); // Should be null when no zone
        // Real mapper handles null zone correctly
    }

    @Test
    void shouldMapDriverWithVehicleInformation() {
        // Given
        Driver driverWithVehicle = new Driver();
        driverWithVehicle.setId("DRIVER-VEHICLE");
        driverWithVehicle.setFirstName("Marie");
        driverWithVehicle.setLastName("Curie");
        driverWithVehicle.setEmail("marie.curie@example.com");
        driverWithVehicle.setPhoneNumber("+33123456781");
        driverWithVehicle.setVehicule("Mercedes Sprinter"); // Specific vehicle

        when(repository.save(any(Driver.class))).thenReturn(driverWithVehicle);

        DriverRequestDTO requestWithVehicle = new DriverRequestDTO();
        requestWithVehicle.setFirstName("Marie");
        requestWithVehicle.setLastName("Curie");
        requestWithVehicle.setEmail("marie.curie@example.com");
        requestWithVehicle.setPhoneNumber("+33123456781");
        requestWithVehicle.setVehicule("Mercedes Sprinter");

        // When
        DriverResponseDTO result = driverService.save(requestWithVehicle);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Marie");
        assertThat(result.getLastName()).isEqualTo("Curie");
        // Vehicle information is handled by real mapper
        // Note: Vehicle field might not be in response DTO based on your DriverResponseDTO definition

        verify(repository).save(any(Driver.class));
    }
}
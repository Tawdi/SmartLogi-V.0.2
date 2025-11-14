package com.smartlogi.smartlogidms.masterdata.zone.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneMapper;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneMapperImpl;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneRequestDTO;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneResponseDTO;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    private ZoneMapper zoneMapper;
    private ZoneServiceImpl zoneService;

    private Zone testZone;
    private ZoneRequestDTO testRequestDTO;
    private ZoneResponseDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        zoneMapper = new ZoneMapperImpl();
        zoneService = new ZoneServiceImpl(zoneRepository, zoneMapper);

        // Test data using setters
        testZone = new Zone();
        testZone.setId("zone-123");
        testZone.setCodePostal("12345678");
        testZone.setName("ZONE-N1");

        testRequestDTO = new ZoneRequestDTO();
        testRequestDTO.setCodePostal("12345678");
        testRequestDTO.setName("ZONE-N1");

        testResponseDTO = new ZoneResponseDTO();
        testResponseDTO.setId("zone-123");
        testResponseDTO.setCodePostal("12345678");
        testResponseDTO.setName("ZONE-N1");
    }

    @Test
    void shouldSaveZoneSuccessfully() {

        ZoneRequestDTO request = new ZoneRequestDTO();
        request.setCodePostal("12345678");
        request.setName("ZONE-N1");

        when(zoneRepository.save(any(Zone.class))).thenReturn(testZone);

        ZoneResponseDTO responseDTO = zoneService.save(request);

        assertThat(responseDTO.getCodePostal()).isEqualTo("12345678");
        assertThat(responseDTO.getName()).isEqualTo("ZONE-N1");
        verify(zoneRepository).save(any(Zone.class));
    }

    @Test
    void shouldUpdateZoneSuccessfully() {

        String zoneId = "zone-123";
        ZoneRequestDTO updateRequest = new ZoneRequestDTO();
        updateRequest.setCodePostal("87654321");
        updateRequest.setName("ZONE-UPDATED");

        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(testZone));
        when(zoneRepository.save(any(Zone.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ZoneResponseDTO result = zoneService.update(zoneId, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("ZONE-UPDATED");
        assertThat(result.getCodePostal()).isEqualTo("87654321");
        verify(zoneRepository).findById(zoneId);
        verify(zoneRepository).save(any(Zone.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentZone() {

        String zoneId = "non-existent";
        ZoneRequestDTO updateRequest = new ZoneRequestDTO();
        updateRequest.setCodePostal("87654321");
        updateRequest.setName("ZONE-UPDATED");

        when(zoneRepository.findById(zoneId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> zoneService.update(zoneId, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + zoneId);

        verify(zoneRepository).findById(zoneId);
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void shouldFindZoneByIdSuccessfully() {

        String zoneId = "zone-123";
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(testZone));

        ZoneResponseDTO result = zoneService.findById(zoneId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(zoneId);
        assertThat(result.getName()).isEqualTo("ZONE-N1");
        verify(zoneRepository).findById(zoneId);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentZone() {

        String zoneId = "non-existent";
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.findById(zoneId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + zoneId);
    }

    @Test
    void shouldFindAllZones() {

        Zone zone2 = new Zone();
        zone2.setId("zone-456");
        zone2.setCodePostal("11111111");
        zone2.setName("ZONE-N2");

        List<Zone> zones = List.of(testZone, zone2);
        when(zoneRepository.findAll()).thenReturn(zones);


        List<ZoneResponseDTO> result = zoneService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("ZONE-N1");
        assertThat(result.get(1).getName()).isEqualTo("ZONE-N2");
        verify(zoneRepository).findAll();
    }

    @Test
    void shouldFindAllZonesWithPagination() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Zone> zonePage = new PageImpl<>(List.of(testZone), pageable, 1);

        when(zoneRepository.findAll(pageable)).thenReturn(zonePage);


        Page<ZoneResponseDTO> result = zoneService.findAll(pageable);


        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("ZONE-N1");
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(zoneRepository).findAll(pageable);
    }

    @Test
    void shouldFindAllZonesWithSpecificationAndPagination() {

        Pageable pageable = PageRequest.of(0, 10);
        Specification<Zone> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("name"), "ZONE-N1");

        Page<Zone> zonePage = new PageImpl<>(List.of(testZone), pageable, 1);

        when(zoneRepository.findAll(spec, pageable)).thenReturn(zonePage);


        Page<ZoneResponseDTO> result = zoneService.findAll(pageable, spec);


        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("ZONE-N1");
        verify(zoneRepository).findAll(spec, pageable);
    }

    @Test
    void shouldDeleteZoneSuccessfully() {

        String zoneId = "zone-123";
        when(zoneRepository.existsById(zoneId)).thenReturn(true);
        doNothing().when(zoneRepository).deleteById(zoneId);


        zoneService.deleteById(zoneId);


        verify(zoneRepository).existsById(zoneId);
        verify(zoneRepository).deleteById(zoneId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentZone() {

        String zoneId = "non-existent";
        when(zoneRepository.existsById(zoneId)).thenReturn(false);

        assertThatThrownBy(() -> zoneService.deleteById(zoneId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + zoneId);

        verify(zoneRepository).existsById(zoneId);
        verify(zoneRepository, never()).deleteById(zoneId);
    }

    @Test
    void shouldCheckIfZoneExists() {

        String zoneId = "zone-123";
        when(zoneRepository.existsById(zoneId)).thenReturn(true);

        boolean exists = zoneService.existsById(zoneId);


        assertThat(exists).isTrue();
        verify(zoneRepository).existsById(zoneId);
    }

    @Test
    void shouldCheckIfZoneDoesNotExist() {

        String zoneId = "non-existent";
        when(zoneRepository.existsById(zoneId)).thenReturn(false);

        boolean exists = zoneService.existsById(zoneId);

        assertThat(exists).isFalse();
        verify(zoneRepository).existsById(zoneId);
    }

    @Test
    void shouldSaveMultipleZonesAndFindThem() {

        ZoneRequestDTO request1 = new ZoneRequestDTO();
        request1.setCodePostal("11111111");
        request1.setName("ZONE-1");

        ZoneRequestDTO request2 = new ZoneRequestDTO();
        request2.setCodePostal("22222222");
        request2.setName("ZONE-2");

        Zone zone1 = new Zone();
        zone1.setId("zone-1");
        zone1.setCodePostal("11111111");
        zone1.setName("ZONE-1");

        Zone zone2 = new Zone();
        zone2.setId("zone-2");
        zone2.setCodePostal("22222222");
        zone2.setName("ZONE-2");

        when(zoneRepository.save(any(Zone.class)))
                .thenReturn(zone1)
                .thenReturn(zone2);

        when(zoneRepository.findAll()).thenReturn(List.of(zone1, zone2));


        ZoneResponseDTO saved1 = zoneService.save(request1);
        ZoneResponseDTO saved2 = zoneService.save(request2);
        List<ZoneResponseDTO> allZones = zoneService.findAll();

        assertThat(saved1.getName()).isEqualTo("ZONE-1");
        assertThat(saved2.getName()).isEqualTo("ZONE-2");
        assertThat(allZones).hasSize(2);
        verify(zoneRepository, times(2)).save(any(Zone.class));
        verify(zoneRepository).findAll();
    }
}
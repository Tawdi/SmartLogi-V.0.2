package com.smartlogi.smartlogidms.masterdata.zone.service;

import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneMapper;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneMapperImpl;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneRequestDTO;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneResponseDTO;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.xmlunit.util.Mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private ZoneMapper zoneMapper ;



    @InjectMocks
    private ZoneServiceImpl zoneService;


    @Test
    public void shouldSaveZone(){

        ZoneRequestDTO request = new ZoneRequestDTO();
        request.setCodePostal("12345678");
        request.setName("ZONE-N1");
        Zone zone = new Zone();
        zone.setCodePostal("12345678");
        zone.setName("ZONE-N1");

        ZoneResponseDTO response =new ZoneResponseDTO();
        response.setCodePostal("12345678");
        response.setName("ZONE-N1");

        when(zoneMapper.toEntity(request)).thenReturn(zone);
        when(zoneMapper.toDto(zone)).thenReturn(response);
        when(zoneRepository.save( any() ) ).thenAnswer(i -> i.getArgument(0));

        ZoneResponseDTO responseDTO = zoneService.save(request);

        assertThat(responseDTO.getCodePostal()).isEqualTo("12345678");
        assertThat(responseDTO.getName()).isEqualTo("ZONE-N1");
    }

}

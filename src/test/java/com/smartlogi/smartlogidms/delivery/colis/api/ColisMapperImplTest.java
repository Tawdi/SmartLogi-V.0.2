package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientMapper;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientMapper;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.shared.api.AdresseMapper;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneMapper;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ColisMapperImplTest {

    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private RecipientMapper recipientMapper;
    @Autowired
    private ZoneMapper zoneMapper;
    @Autowired
    private AdresseMapper adresseMapper;

    @Autowired
    private  ColisMapper mapper ;

    @BeforeEach
    void setup(){
    }

    private Colis colis = new Colis();
    private ColisRequestDTO request;
    private ColisResponseDTO response;

    @Test
    public void testToDTO() {
        colis = new Colis();
        colis.setId("COL-1");
        colis.setAdresseLivraison(new Adresse("safi","sa3Ada 123 ",null));
        colis.setReference("AZERT-23456789");
        colis.setPoids(2.3);
        colis.setDescription("djaja ");
        colis.setPriorite(Colis.Priorite.MEDIUM );
        colis.setStatut( Colis.ColisStatus.CREATED );

        ColisResponseDTO nullResp = mapper.toDto(null);

        response = mapper.toDto(colis);
    }

    @Test
    public void testToEntity() {
        request = new ColisRequestDTO();
        request.setZoneId("ZONE-234");
        request.setExpediteurId("EXP-2345");
        request.setDestinataireId("DES-2345");
        request.setReference("AZERT-23456789");
        request.setPoids(2.3);
        request.setDescription("djaja ");
        request.setPriorite(Colis.Priorite.MEDIUM );

        Colis nullColis = mapper.toEntity(null);

        Colis colis = mapper.toEntity(request);
    }


    @Test
    public void testUpdateEntityFromDto() {
        request = new ColisRequestDTO();
        request.setZoneId("ZONE-234");
        request.setExpediteurId(null);
        request.setDestinataireId("DES-2345");
        request.setReference("AZERT-23456789");
        request.setPoids(2.3);
        request.setDescription("djaja ");
        request.setPriorite(Colis.Priorite.MEDIUM );

        mapper.updateEntityFromDto(null,colis);

        mapper.updateEntityFromDto(request,colis);

    }
}

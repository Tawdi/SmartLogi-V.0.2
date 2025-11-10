package com.smartlogi.smartlogidms.delivery.colis.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.EmailService;
import com.smartlogi.smartlogidms.delivery.colis.api.*;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.colis.domain.ColisProduit;
import com.smartlogi.smartlogidms.delivery.colis.domain.ColisRepository;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonMapper;
import com.smartlogi.smartlogidms.delivery.historique.domain.HistoriqueLivraison;
import com.smartlogi.smartlogidms.delivery.historique.domain.HistoriqueLivraisonRepository;
import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import com.smartlogi.smartlogidms.delivery.product.domain.ProductRrepository;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverResponseDTO;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import com.smartlogi.smartlogidms.masterdata.driver.domain.DriverRepository;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.RecipientRepository;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ColisServiceTest {


    @Mock
    private ClientExpediteurRepository expediteurRepo;
    @Mock
    private RecipientRepository destinataireRepo;
    @Mock
    private ZoneRepository zoneRepo;
    @Mock
    private DriverRepository driverRepo;
    @Mock
    private ProductRrepository productRrepo;
    @Mock
    private EmailService emailService;
    @Mock
    private HistoriqueLivraisonRepository historyRepo;
    @Mock
    private HistoriqueLivraisonMapper historyMapper;

    @Mock
    private ColisRepository colisRepository;
    @Mock
    private ColisMapper colisMapper;

    @InjectMocks
    private ColisServiceImpl colisService;

    private ColisRequestDTO request;
    private Colis colis;
    private ClientExpediteur expediteur;
    private Recipient destinataire;
    private Zone zone;
    private Driver driver;
    private Product product, product2;


    @Test
    public void shouldSaveColisWithProducts() {

        request = new ColisRequestDTO();
        request.setReference("REF123");
        request.setPoids(1.5);
        request.setPriorite(Colis.Priorite.MEDIUM);
        request.setDestinataireId("DEST1");
        request.setExpediteurId("EXP1");
        request.setZoneId("ZONE1");
        request.setProductList(
                List.of(
                        new ColisRequestDTO.ProduitColisDTO("PROD1", 2, 100.0),
                        new ColisRequestDTO.ProduitColisDTO("PROD2", 1, 50.0)
                )
        );

        expediteur = new ClientExpediteur();
        expediteur.setId("EXP1");
        expediteur.setEmail("exp@test.com");
        destinataire = new Recipient();
        destinataire.setId("DEST1");
        destinataire.setEmail("dest@test.com");
        zone = new Zone();
        zone.setId("ZONE1");

        // first product
        product = new Product();
        product.setId("PROD1");
        product.setNom("Livre");
        // second product
        product2 = new Product();
        product2.setId("PROD2");
        product2.setNom("Phone");

        when(expediteurRepo.findById("EXP1")).thenReturn(Optional.of(expediteur));
        when(destinataireRepo.findById("DEST1")).thenReturn(Optional.of(destinataire));
        when(zoneRepo.findById("ZONE1")).thenReturn(Optional.of(zone));
        when(productRrepo.findById("PROD1")).thenReturn(Optional.of(product));
        when(productRrepo.findById("PROD2")).thenReturn(Optional.of(product2));

        colis = new Colis();
        colis.setId("COLIS1");
        colis.setReference("REF123");
        colis.setPoids(1.5);
        colis.setPriorite(Colis.Priorite.MEDIUM);

        when(colisMapper.toEntity(request)).thenReturn(colis);
        when(colisRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(colisMapper.toDto(any())).thenReturn(new ColisResponseDTO());
        ColisResponseDTO result = colisService.save(request);

        verify(colisRepository, times(2)).save(any());
        verify(emailService, times(2)).sendNotification(anyString(), anyString(), anyString());
        verify(productRrepo).findById("PROD1");
        assertThat(colis.getColisProduits()).hasSize(2);
        assertThat(colis.getColisProduits())
                .hasSize(2)
                .extracting(ColisProduit::getQuantite, ColisProduit::getPrixUnitaire)
                .containsExactlyInAnyOrder(
                        tuple(2, 100.0),
                        tuple(1, 50.0)
                );
    }

    @Test
    void shouldThrowWhenProductNotFound() {
        ColisRequestDTO request = new ColisRequestDTO();
        request.setExpediteurId("EXP1");
        request.setDestinataireId("DEST1");
        request.setZoneId("ZONE1");
        request.setProductList(List.of(
                new ColisRequestDTO.ProduitColisDTO("UNKNOWN", 1, 50.0)
        ));

        Colis colis = new Colis();
        colis.setId("C1");

        when(colisMapper.toEntity(request)).thenReturn(colis);
        when(expediteurRepo.findById("EXP1")).thenReturn(Optional.of(mock(ClientExpediteur.class)));
        when(destinataireRepo.findById("DEST1")).thenReturn(Optional.of(mock(Recipient.class)));
        when(zoneRepo.findById("ZONE1")).thenReturn(Optional.of(mock(Zone.class)));
        when(productRrepo.findById("UNKNOWN")).thenReturn(Optional.empty());
        when(colisRepository.save(colis)).thenAnswer(i -> i.getArgument(0));

        assertThatThrownBy(() -> colisService.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Produit non trouvé: UNKNOWN");
    }

    @Test
    void shouldUpdateStatusWithValidTransition() {
        // Given
        ClientExpediteur expediteur = new ClientExpediteur();
        expediteur.setId("EXP1");
        expediteur.setEmail("exp@test.com");

        Colis colis = new Colis();
        colis.setId("C1");
        colis.setStatut(Colis.ColisStatus.CREATED);
        colis.setExpediteur(expediteur);

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatut(Colis.ColisStatus.COLLECTED);
        request.setUtilisateurId("USER1");
        request.setCommentaire("Ramassage effectué");

        when(colisRepository.findById("C1")).thenReturn(Optional.of(colis));
        when(colisRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        ColisResponseDTO responseDTO = new ColisResponseDTO();
        responseDTO.setStatut(Colis.ColisStatus.COLLECTED);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(responseDTO);
        // When
        ColisResponseDTO result = colisService.updateStatus("C1", request);

        // Then
        verify(historyRepo).save(any(HistoriqueLivraison.class));
        verify(emailService).sendNotification(anyString(), anyString(), anyString());

        assertThat(result.getStatut()).isEqualTo(Colis.ColisStatus.COLLECTED);
    }


    @Test
    void shouldThrowOnInvalidStatusTransition() {
        // === GIVEN ===
        Colis colis = new Colis();
        colis.setId("C1");
        colis.setStatut(Colis.ColisStatus.IN_TRANSIT);

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatut(Colis.ColisStatus.CREATED); // ← Transition invalide

        // === MOCK ===
        when(colisRepository.findById("C1")).thenReturn(Optional.of(colis));

        // === WHEN & THEN ===
        assertThatThrownBy(() -> colisService.updateStatus("C1", request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition")
                .hasMessageContaining("IN_TRANSIT → CREATED");
    }

    @Test
    void shouldAssignDriverWhenNoneAssigned() {
        // === GIVEN ===
        Colis colis = new Colis();
        colis.setId("C1");
        colis.setStatut(Colis.ColisStatus.IN_STOCK);
        colis.setLivreur(null); // aucun livreur

         driver = new Driver();
        driver.setId("DRV1");

        AssignerLivreurRequestDTO request = new AssignerLivreurRequestDTO("DRV1");

        // === MOCKS ===
        when(colisRepository.findById("C1")).thenReturn(Optional.of(colis));
        when(driverRepo.findById("DRV1")).thenReturn(Optional.of(driver));
        when(colisRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ColisResponseDTO responseDTO = new ColisResponseDTO();
        DriverResponseDTO driverResponseDTO = new DriverResponseDTO();
        driverResponseDTO.setId("DRV1");
        responseDTO.setLivreur( driverResponseDTO);
        when(colisMapper.toDto(any())).thenReturn(responseDTO);

        ColisResponseDTO result = colisService.assignerLivreur("C1", request);

        verify(historyRepo).save(argThat(h ->
                h.getCommentaire().contains("Assigned to driver ID=DRV1")
        ));
        verify(colisRepository).save(colis);
        assertThat(result.getLivreur().getId()).isEqualTo("DRV1");
    }

    @Test
    void shouldReassignDriverWhenAlreadyAssigned() {
        // === GIVEN ===
        Driver oldDriver = new Driver(); oldDriver.setId("OLD1");
        Driver newDriver = new Driver(); newDriver.setId("NEW1");

        Colis colis = new Colis();
        colis.setId("C2");
        colis.setLivreur(oldDriver);

        AssignerLivreurRequestDTO request = new AssignerLivreurRequestDTO("NEW1");

        // === MOCKS ===
        when(colisRepository.findById("C2")).thenReturn(Optional.of(colis));
        when(driverRepo.findById("NEW1")).thenReturn(Optional.of(newDriver));
        when(colisRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ColisResponseDTO dto = new ColisResponseDTO();
        DriverResponseDTO driverResponseDTO = new DriverResponseDTO();
        driverResponseDTO.setId("NEW1");
        dto.setLivreur( driverResponseDTO);
        when(colisMapper.toDto(any())).thenReturn(dto);

        // === WHEN ===
        ColisResponseDTO result = colisService.assignerLivreur("C2", request);

        // === THEN ===
        verify(historyRepo).save(argThat(h ->
                h.getCommentaire().contains("Reassigned from driver ID=OLD1 to driver ID=NEW1")
        ));
        assertThat(result.getLivreur().getId()).isEqualTo("NEW1");
    }
}

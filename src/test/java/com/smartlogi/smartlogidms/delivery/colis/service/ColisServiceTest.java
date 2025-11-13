package com.smartlogi.smartlogidms.delivery.colis.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.EmailService;
import com.smartlogi.smartlogidms.delivery.colis.api.*;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.colis.domain.ColisProduit;
import com.smartlogi.smartlogidms.delivery.colis.domain.ColisRepository;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonMapper;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonResponseDTO;
import com.smartlogi.smartlogidms.delivery.historique.domain.HistoriqueLivraison;
import com.smartlogi.smartlogidms.delivery.historique.domain.HistoriqueLivraisonRepository;
import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import com.smartlogi.smartlogidms.delivery.product.domain.ProductRrepository;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import com.smartlogi.smartlogidms.masterdata.driver.domain.DriverRepository;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.RecipientRepository;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColisServiceTest {

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
    private ColisRepository colisRepository;

    // Use mock mappers instead of real ones to avoid dependency issues
    @Mock
    private ColisMapper colisMapper;

    @Mock
    private HistoriqueLivraisonMapper historyMapper;

    private ColisServiceImpl colisService;

    private ClientExpediteur expediteur;
    private Recipient destinataire;
    private Zone zone;
    private Driver driver;
    private Product product1, product2;
    private Colis colis;
    private ColisResponseDTO colisResponseDTO;

    @BeforeEach
    void setUp() {
        colisService = new ColisServiceImpl(
                colisRepository, colisMapper, destinataireRepo, expediteurRepo,
                zoneRepo, driverRepo, emailService, productRrepo, historyRepo, historyMapper
        );

        // Initialize test data using setters
        expediteur = new ClientExpediteur();
        expediteur.setId("EXP1");
        expediteur.setEmail("expediteur@test.com");
        expediteur.setFirstName("Expediteur Test");

        destinataire = new Recipient();
        destinataire.setId("DEST1");
        destinataire.setEmail("destinataire@test.com");
        destinataire.setFirstName("Destinataire Test");

        zone = new Zone();
        zone.setId("ZONE1");
        zone.setName("Zone Test");

        driver = new Driver();
        driver.setId("DRV1");
        driver.setFirstName("Driver Test");

        product1 = new Product();
        product1.setId("PROD1");
        product1.setNom("Laptop");
        product1.setCategorie("Electronics");
        product1.setPoids(2.5);

        product2 = new Product();
        product2.setId("PROD2");
        product2.setNom("Livre");
        product2.setCategorie("Books");
        product2.setPoids(0.5);

        colis = new Colis();
        colis.setId("COLIS1");
        colis.setReference("REF123");
        colis.setPoids(3.0);
        colis.setPriorite(Colis.Priorite.HIGH);
        colis.setStatut(Colis.ColisStatus.CREATED);
        colis.setExpediteur(expediteur);
        colis.setDestinataire(destinataire);
        colis.setZone(zone);

        colisResponseDTO = new ColisResponseDTO();
        colisResponseDTO.setId("COLIS1");
        colisResponseDTO.setReference("REF123");
        colisResponseDTO.setPoids(3.0);
        colisResponseDTO.setPriorite(Colis.Priorite.HIGH);
        colisResponseDTO.setStatut(Colis.ColisStatus.CREATED);
    }

    @Test
    void shouldSaveColisWithProductsSuccessfully() {
        // Given
        ColisRequestDTO request = new ColisRequestDTO();
        request.setReference("REF123");
        request.setPoids(3.0);
        request.setPriorite(Colis.Priorite.HIGH);
        request.setExpediteurId("EXP1");
        request.setDestinataireId("DEST1");
        request.setZoneId("ZONE1");
        request.setProductList(List.of(
                new ColisRequestDTO.ProduitColisDTO("PROD1", 2, 1500.0),
                new ColisRequestDTO.ProduitColisDTO("PROD2", 1, 25.0)
        ));

        when(expediteurRepo.findById("EXP1")).thenReturn(Optional.of(expediteur));
        when(destinataireRepo.findById("DEST1")).thenReturn(Optional.of(destinataire));
        when(zoneRepo.findById("ZONE1")).thenReturn(Optional.of(zone));
        when(productRrepo.findById("PROD1")).thenReturn(Optional.of(product1));
        when(productRrepo.findById("PROD2")).thenReturn(Optional.of(product2));

        // Mock the mapper behavior
        when(colisMapper.toEntity(request)).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisResponseDTO);

        // Mock repository save to return the colis with products
        Colis colisWithProducts = new Colis();
        colisWithProducts.setId("COLIS1");
        colisWithProducts.setReference("REF123");
        colisWithProducts.setExpediteur(expediteur);
        colisWithProducts.setDestinataire(destinataire);
        colisWithProducts.getColisProduits().add(new ColisProduit());
        colisWithProducts.getColisProduits().add(new ColisProduit());

        when(colisRepository.save(any(Colis.class))).thenReturn(colisWithProducts);

        // When
        ColisResponseDTO result = colisService.save(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getReference()).isEqualTo("REF123");

        verify(colisRepository, times(2)).save(any(Colis.class));
        verify(emailService, times(2)).sendNotification(anyString(), anyString(), anyString());
        verify(productRrepo, times(2)).findById(anyString());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        ColisRequestDTO request = new ColisRequestDTO();
        request.setExpediteurId("EXP1");
        request.setDestinataireId("DEST1");
        request.setZoneId("ZONE1");
        request.setProductList(List.of(
                new ColisRequestDTO.ProduitColisDTO("UNKNOWN", 1, 50.0)
        ));

        when(expediteurRepo.findById("EXP1")).thenReturn(Optional.of(expediteur));
        when(destinataireRepo.findById("DEST1")).thenReturn(Optional.of(destinataire));
        when(zoneRepo.findById("ZONE1")).thenReturn(Optional.of(zone));
        when(productRrepo.findById("UNKNOWN")).thenReturn(Optional.empty());

        when(colisMapper.toEntity(request)).thenReturn(colis);

        when(colisRepository.save(any(Colis.class))).thenReturn(colis);

        // When & Then
        assertThatThrownBy(() -> colisService.save(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Produit non trouvé: UNKNOWN");

        verify(colisRepository, times(1)).save(any(Colis.class));
    }

    @Test
    void shouldUpdateColisSuccessfully() {
        // Given
        String colisId = "COLIS1";
        ColisRequestDTO updateRequest = new ColisRequestDTO();
        updateRequest.setReference("REF_UPDATED");
        updateRequest.setPoids(4.0);
        updateRequest.setPriorite(Colis.Priorite.MEDIUM);
        updateRequest.setExpediteurId("EXP1");
        updateRequest.setDestinataireId("DEST1");
        updateRequest.setZoneId("ZONE1");

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(expediteurRepo.findById("EXP1")).thenReturn(Optional.of(expediteur));
        when(destinataireRepo.findById("DEST1")).thenReturn(Optional.of(destinataire));
        when(zoneRepo.findById("ZONE1")).thenReturn(Optional.of(zone));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisResponseDTO);

        // When
        ColisResponseDTO result = colisService.update(colisId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(colisRepository).save(any(Colis.class));
    }


    @Test
    void shouldAllowTransitionFromCREATEDtoCOLLECTED() {
        // Given
        String colisId = "COLIS1";
        UpdateStatusRequest statusRequest = new UpdateStatusRequest();
        statusRequest.setStatut(Colis.ColisStatus.COLLECTED);
        statusRequest.setUtilisateurId("USER1");
        statusRequest.setCommentaire("Collected");

        colis.setStatut(Colis.ColisStatus.CREATED);

        ColisResponseDTO updatedResponse = new ColisResponseDTO();
        updatedResponse.setId("COLIS1");
        updatedResponse.setStatut(Colis.ColisStatus.COLLECTED);

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(updatedResponse);

        // When
        ColisResponseDTO result = colisService.updateStatus(colisId, statusRequest);

        // Then
        assertThat(result.getStatut()).isEqualTo(Colis.ColisStatus.COLLECTED);
        verify(colisRepository).save(any(Colis.class));
    }

    @Test
    void shouldAllowTransitionFromCOLLECTEDtoIN_STOCK() {
        // Given
        String colisId = "COLIS1";
        UpdateStatusRequest statusRequest = new UpdateStatusRequest();
        statusRequest.setStatut(Colis.ColisStatus.IN_STOCK);
        statusRequest.setUtilisateurId("USER1");
        statusRequest.setCommentaire("Placed in stock");

        colis.setStatut(Colis.ColisStatus.COLLECTED);

        ColisResponseDTO updatedResponse = new ColisResponseDTO();
        updatedResponse.setId("COLIS1");
        updatedResponse.setStatut(Colis.ColisStatus.IN_STOCK);

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(updatedResponse);

        // When
        ColisResponseDTO result = colisService.updateStatus(colisId, statusRequest);

        // Then
        assertThat(result.getStatut()).isEqualTo(Colis.ColisStatus.IN_STOCK);
    }

    @Test
    void shouldAllowTransitionFromIN_STOCKtoIN_TRANSIT() {
        // Given
        String colisId = "COLIS1";
        UpdateStatusRequest statusRequest = new UpdateStatusRequest();
        statusRequest.setStatut(Colis.ColisStatus.IN_TRANSIT);
        statusRequest.setUtilisateurId("USER1");
        statusRequest.setCommentaire("In transit");

        colis.setStatut(Colis.ColisStatus.IN_STOCK);

        ColisResponseDTO updatedResponse = new ColisResponseDTO();
        updatedResponse.setId("COLIS1");
        updatedResponse.setStatut(Colis.ColisStatus.IN_TRANSIT);

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(updatedResponse);

        // When
        ColisResponseDTO result = colisService.updateStatus(colisId, statusRequest);

        // Then
        assertThat(result.getStatut()).isEqualTo(Colis.ColisStatus.IN_TRANSIT);
    }

    @Test
    void shouldAllowTransitionFromIN_TRANSITtoDELIVERED() {
        // Given
        String colisId = "COLIS1";
        UpdateStatusRequest statusRequest = new UpdateStatusRequest();
        statusRequest.setStatut(Colis.ColisStatus.DELIVERED);
        statusRequest.setUtilisateurId("USER1");
        statusRequest.setCommentaire("Delivered");

        colis.setStatut(Colis.ColisStatus.IN_TRANSIT);

        ColisResponseDTO updatedResponse = new ColisResponseDTO();
        updatedResponse.setId("COLIS1");
        updatedResponse.setStatut(Colis.ColisStatus.DELIVERED);

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(updatedResponse);

        // When
        ColisResponseDTO result = colisService.updateStatus(colisId, statusRequest);

        // Then
        assertThat(result.getStatut()).isEqualTo(Colis.ColisStatus.DELIVERED);
    }

    @Test
    void shouldPreventTransitionFromDELIVERED() {
        // Given
        String colisId = "COLIS1";
        UpdateStatusRequest statusRequest = new UpdateStatusRequest();
        statusRequest.setStatut(Colis.ColisStatus.IN_TRANSIT); // Any status except DELIVERED

        colis.setStatut(Colis.ColisStatus.DELIVERED);

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));

        // When & Then
        assertThatThrownBy(() -> colisService.updateStatus(colisId, statusRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition: DELIVERED → IN_TRANSIT");
    }

    @Test
    void shouldHandleDefaultCaseInSwitch() {
        // Given
        String colisId = "COLIS1";
        UpdateStatusRequest statusRequest = new UpdateStatusRequest();
        statusRequest.setStatut(Colis.ColisStatus.COLLECTED);

        // Set an invalid status using reflection to test default case
        colis.setStatut(null); // This will trigger the default case

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));

        // When & Then
        assertThatThrownBy(() -> colisService.updateStatus(colisId, statusRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Invalid status transition (cuerrent status is missing)");
    }

    @Test
    void shouldAssignDriverSuccessfully() {
        // Given
        String colisId = "COLIS1";
        AssignerLivreurRequestDTO assignRequest = new AssignerLivreurRequestDTO("DRV1");

        colis.setLivreur(null); // No driver assigned initially

        ColisResponseDTO responseWithDriver = new ColisResponseDTO();
        responseWithDriver.setId("COLIS1");

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(driverRepo.findById("DRV1")).thenReturn(Optional.of(driver));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(responseWithDriver);

        // When
        ColisResponseDTO result = colisService.assignerLivreur(colisId, assignRequest);

        // Then
        assertThat(result).isNotNull();
        verify(historyRepo).save(any(HistoriqueLivraison.class));
        verify(colisRepository).save(any(Colis.class));
    }

    @Test
    void shouldReassignDriverSuccessfully() {
        // Given
        String colisId = "COLIS1";
        AssignerLivreurRequestDTO assignRequest = new AssignerLivreurRequestDTO("DRV_NEW");

        Driver oldDriver = new Driver();
        oldDriver.setId("DRV_OLD");
        colis.setLivreur(oldDriver);

        Driver newDriver = new Driver();
        newDriver.setId("DRV_NEW");

        ColisResponseDTO responseWithNewDriver = new ColisResponseDTO();
        responseWithNewDriver.setId("COLIS1");

        when(colisRepository.findById(colisId)).thenReturn(Optional.of(colis));
        when(driverRepo.findById("DRV_NEW")).thenReturn(Optional.of(newDriver));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(responseWithNewDriver);

        // When
        ColisResponseDTO result = colisService.assignerLivreur(colisId, assignRequest);

        // Then
        assertThat(result).isNotNull();
        verify(historyRepo).save(argThat(history ->
                history.getCommentaire().contains("Reassigned from driver ID=DRV_OLD to driver ID=DRV_NEW")
        ));
    }

    @Test
    void shouldFindByExpediteurIdWithStatus_DELIVERED() {
        // Given
        String expediteurId = "EXP1";
        Colis.ColisStatus status = Colis.ColisStatus.DELIVERED;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Colis> colisPage = new PageImpl<>(List.of(colis));
        when(colisRepository.findByExpediteurId(expediteurId, status, pageable)).thenReturn(colisPage);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisResponseDTO);

        // When
        Page<ColisResponseDTO> result = colisService.findByExpediteurId(expediteurId, status, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(colisRepository).findByExpediteurId(expediteurId, status, pageable);
    }

    @Test
    void shouldFindByExpediteurIdWithStatus_NULL() {
        // Given
        String expediteurId = "EXP1";
        Colis.ColisStatus status = null;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Colis> colisPage = new PageImpl<>(List.of(colis));
        when(colisRepository.findByExpediteurId(expediteurId, status, pageable)).thenReturn(colisPage);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisResponseDTO);

        // When
        Page<ColisResponseDTO> result = colisService.findByExpediteurId(expediteurId, status, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(colisRepository).findByExpediteurId(expediteurId, status, pageable);
    }

    @Test
        // inProgressStatuses
    void shouldFindByExpediteurIdWithStatus_NOT_DELIVERED() {
        // Given
        String expediteurId = "EXP1";
        Colis.ColisStatus status = Colis.ColisStatus.COLLECTED;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Colis> colisPage = new PageImpl<>(List.of(colis));
        List<Colis.ColisStatus> inProgressStatuses = List.of(
                Colis.ColisStatus.CREATED,
                Colis.ColisStatus.COLLECTED,
                Colis.ColisStatus.IN_STOCK,
                Colis.ColisStatus.IN_TRANSIT
        );
        when(colisRepository.findByExpediteurIdAndStatuts(expediteurId, inProgressStatuses, pageable)).thenReturn(colisPage);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisResponseDTO);

        // When
        Page<ColisResponseDTO> result = colisService.findByExpediteurId(expediteurId, status, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(colisRepository).findByExpediteurIdAndStatuts(expediteurId, inProgressStatuses, pageable);
    }

    @Test
    void shouldFindByDestinataireId() {
        // Given
        String destinataireId = "DEST1";
        Colis.ColisStatus status = Colis.ColisStatus.IN_TRANSIT;
        Pageable pageable = PageRequest.of(0, 10);

        Page<Colis> colisPage = new PageImpl<>(List.of(colis));
        when(colisRepository.findByDestinataireId(destinataireId, status, pageable)).thenReturn(colisPage);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisResponseDTO);

        // When
        Page<ColisResponseDTO> result = colisService.findByDestinataireId(destinataireId, status, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(colisRepository).findByDestinataireId(destinataireId, status, pageable);
    }

    @Test
    void shouldFindByLivreurId() {
        // Given
        String livreurId = "DRV1";
        Colis.ColisStatus status = Colis.ColisStatus.IN_TRANSIT;
        Pageable pageable = PageRequest.of(0, 10);

        colis.setLivreur(driver);
        Page<Colis> colisPage = new PageImpl<>(List.of(colis));
        when(colisRepository.findByLivreurId(livreurId, status, pageable)).thenReturn(colisPage);
        when(colisMapper.toDto(any(Colis.class))).thenReturn(colisResponseDTO);

        // When
        Page<ColisResponseDTO> result = colisService.findByLivreurId(livreurId, status, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(colisRepository).findByLivreurId(livreurId, status, pageable);
    }

    @Test
    void shouldGetColisHistory() {
        // Given
        String colisId = "COLIS1";
        Pageable pageable = PageRequest.of(0, 10);

        HistoriqueLivraison history = new HistoriqueLivraison();
        history.setId("HIST1");
        Page<HistoriqueLivraison> historyPage = new PageImpl<>(List.of(history));

        HistoriqueLivraisonResponseDTO historyResponse = new HistoriqueLivraisonResponseDTO();
        historyResponse.setId("HIST1");

        when(historyRepo.findByColisId(colisId, pageable)).thenReturn(historyPage);
        when(historyMapper.toDto(any(HistoriqueLivraison.class))).thenReturn(historyResponse);

        // When
        Page<HistoriqueLivraisonResponseDTO> result = colisService.getHistory(colisId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(historyRepo).findByColisId(colisId, pageable);
    }

    @Test
    void shouldGetProductsByColisId() {
        // Given
        String colisId = "COLIS1";
        Pageable pageable = PageRequest.of(0, 10);

        ColisProduit colisProduit = new ColisProduit();
        colisProduit.setProduct(product1);
        colisProduit.setQuantite(2);
        colisProduit.setPrixUnitaire(1500.0);

        Page<ColisProduit> produitsPage = new PageImpl<>(List.of(colisProduit));
        when(colisRepository.findColisProduitsByColisId(colisId, pageable)).thenReturn(produitsPage);

        // When
        Page<ColisProductResponseDTO> result = colisService.getProductsByColisId(colisId, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getProductId()).isEqualTo("PROD1");
        assertThat(result.getContent().get(0).getPrixTotal()).isEqualTo(3000.0);
        verify(colisRepository).findColisProduitsByColisId(colisId, pageable);
    }

    @Test
    void shouldGetSyntheseByZone() {
        // Given
        SyntheseDTO<String> synthese = new SyntheseDTO<>("ZONE1", 5L, 10);
        when(colisRepository.countByZone()).thenReturn(List.of(synthese));

        // When
        List<SyntheseDTO<String>> result = colisService.getSyntheseByZone();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).key()).isEqualTo("ZONE1");
        assertThat(result.get(0).count()).isEqualTo(5L);
    }

    @Test
    void shouldGetSyntheseByStatut() {
        // Given
        SyntheseDTO<Colis.ColisStatus> synthese = new SyntheseDTO<>(Colis.ColisStatus.DELIVERED, 10L, 10);
        when(colisRepository.countByStatut()).thenReturn(List.of(synthese));

        // When
        List<SyntheseDTO<Colis.ColisStatus>> result = colisService.getSyntheseByStatut();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).key()).isEqualTo(Colis.ColisStatus.DELIVERED);
        assertThat(result.get(0).count()).isEqualTo(10L);
    }

    @Test
    void shouldGetSyntheseByPriorite() {
        // Given
        SyntheseDTO<Colis.Priorite> synthese = new SyntheseDTO<>(Colis.Priorite.HIGH, 3L, 10);
        when(colisRepository.countByPriorite()).thenReturn(List.of(synthese));

        // When
        List<SyntheseDTO<Colis.Priorite>> result = colisService.getSyntheseByPriorite();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).key()).isEqualTo(Colis.Priorite.HIGH);
        assertThat(result.get(0).count()).isEqualTo(3L);
    }

    @Test
    void shouldThrowExceptionWhenColisNotFound() {
        // Given
        String nonExistentId = "NON_EXISTENT";
        when(colisRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> colisService.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);
    }
}
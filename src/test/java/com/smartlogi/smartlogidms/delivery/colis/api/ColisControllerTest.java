package com.smartlogi.smartlogidms.delivery.colis.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.exception.GlobalExceptionHandler;
import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.colis.service.ColisService;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonResponseDTO;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverResponseDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.shared.api.AdresseDTO;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ColisControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ColisService colisService;

    @Mock
    private ColisMapper colisMapper;

    @InjectMocks
    private ColisController colisController;

    private ObjectMapper objectMapper;

    private ColisRequestDTO validColisRequest;
    private ColisResponseDTO colisResponse;
    private Colis colisEntity;

    @BeforeEach
    void setUp() {
        // Configure MockMvc with Pageable support and GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(colisController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();

        // Setup test data for request
        validColisRequest = new ColisRequestDTO();
        validColisRequest.setReference("COLIS-001");
        validColisRequest.setPoids(5.0);
        validColisRequest.setDescription("Fragile items");
        validColisRequest.setPriorite(Colis.Priorite.HIGH);
        validColisRequest.setExpediteurId("client-123");
        validColisRequest.setDestinataireId("recipient-456");
        validColisRequest.setZoneId("zone-789");
        validColisRequest.setVille("Paris");
        validColisRequest.setRue("123 Main Street");
        validColisRequest.setCodePostal("75001");

        // Add products to the request
        ColisRequestDTO.ProduitColisDTO product1 = new ColisRequestDTO.ProduitColisDTO("product-1", 2, 99.99);
        ColisRequestDTO.ProduitColisDTO product2 = new ColisRequestDTO.ProduitColisDTO("product-2", 1, 49.99);
        validColisRequest.setProductList(List.of(product1, product2));

        // Setup test data for response
        colisResponse = new ColisResponseDTO();
        colisResponse.setId("colis-123");
        colisResponse.setReference("COLIS-001");
        colisResponse.setPoids(5.0);
        colisResponse.setDescription("Fragile items");
        colisResponse.setStatut(Colis.ColisStatus.CREATED);
        colisResponse.setPriorite(Colis.Priorite.HIGH);

        // Setup related entities
        ClientResponseDTO expediteur = new ClientResponseDTO();
        expediteur.setId("client-123");
        expediteur.setFirstName("Company");
        expediteur.setLastName("Corp");
        colisResponse.setExpediteur(expediteur);

        RecipientResponseDTO destinataire = new RecipientResponseDTO();
        destinataire.setId("recipient-456");
        destinataire.setFirstName("John");
        destinataire.setLastName("Doe");
        colisResponse.setDestinataire(destinataire);

        ZoneResponseDTO zone = new ZoneResponseDTO();
        zone.setId("zone-789");
        zone.setName("Zone Paris Center");
        colisResponse.setZone(zone);

        AdresseDTO adresse = new AdresseDTO();
        adresse.setVille("Paris");
        adresse.setRue("123 Main Street");
        adresse.setCodePostal("75001");
        colisResponse.setAdresseLivraison(adresse);

        // Setup entity
        colisEntity = new Colis();
        colisEntity.setId("colis-123");
        colisEntity.setReference("COLIS-001");
        colisEntity.setPoids(5.0);
        colisEntity.setDescription("Fragile items");
        colisEntity.setStatut(Colis.ColisStatus.CREATED);
        colisEntity.setPriorite(Colis.Priorite.HIGH);
    }

    @Test
    void createColis_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        when(colisService.save(any(ColisRequestDTO.class))).thenReturn(colisResponse);

        // When & Then
        mockMvc.perform(post("/api/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource created successfully"))
                .andExpect(jsonPath("$.data.id").value("colis-123"))
                .andExpect(jsonPath("$.data.reference").value("COLIS-001"))
                .andExpect(jsonPath("$.data.poids").value(5.0))
                .andExpect(jsonPath("$.data.description").value("Fragile items"))
                .andExpect(jsonPath("$.data.statut").value("CREATED"))
                .andExpect(jsonPath("$.data.priorite").value("HIGH"))
                .andExpect(jsonPath("$.data.expediteur.id").value("client-123"))
                .andExpect(jsonPath("$.data.destinataire.id").value("recipient-456"))
                .andExpect(jsonPath("$.data.zone.id").value("zone-789"))
                .andExpect(jsonPath("$.data.adresseLivraison.ville").value("Paris"))
                .andExpect(jsonPath("$.data.adresseLivraison.rue").value("123 Main Street"))
                .andExpect(jsonPath("$.data.adresseLivraison.codePostal").value("75001"));

        verify(colisService, times(1)).save(any(ColisRequestDTO.class));
    }

    @Test
    void createColis_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        ColisRequestDTO invalidRequest = new ColisRequestDTO();
        invalidRequest.setReference(""); // Blank reference
        invalidRequest.setPoids(-1.0); // Negative weight
        invalidRequest.setPriorite(null); // Null priority
        invalidRequest.setExpediteurId(null); // Null expediteur
        invalidRequest.setDestinataireId(null); // Null destinataire
        invalidRequest.setZoneId(null); // Null zone
        invalidRequest.setVille(""); // Blank ville
        invalidRequest.setCodePostal(""); // Blank code postal

        // When & Then
        mockMvc.perform(post("/api/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(colisService, never()).save(any(ColisRequestDTO.class));
    }

    @Test
    void getColisById_WithValidId_ShouldReturnColis() throws Exception {
        // Given
        String colisId = "colis-123";
        when(colisService.findById(colisId)).thenReturn(colisResponse);

        // When & Then
        mockMvc.perform(get("/api/colis/{id}", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value("colis-123"))
                .andExpect(jsonPath("$.data.reference").value("COLIS-001"))
                .andExpect(jsonPath("$.data.poids").value(5.0))
                .andExpect(jsonPath("$.data.statut").value("CREATED"));

        verify(colisService, times(1)).findById(colisId);
    }

    @Test
    void getColisById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        when(colisService.findById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Colis not found with id: " + nonExistentId));

        // When & Then
        mockMvc.perform(get("/api/colis/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Resource not found: Colis not found with id: " + nonExistentId));

        verify(colisService, times(1)).findById(nonExistentId);
    }

    @Test
    void getParcelsByClient_WithStatusFilter_ShouldReturnFilteredResults() throws Exception {
        // Given
        String expediteurId = "client-123";
        Colis.ColisStatus status = Colis.ColisStatus.IN_TRANSIT;
        Page<ColisResponseDTO> page = new PageImpl<>(List.of(colisResponse), PageRequest.of(0, 10), 1);
        when(colisService.findByExpediteurId(eq(expediteurId), eq(status), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/colis/client/{expediteurId}", expediteurId)
                        .param("status", "IN_TRANSIT")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Parcels retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value("colis-123"))
                .andExpect(jsonPath("$.data.content[0].reference").value("COLIS-001"));

        verify(colisService, times(1)).findByExpediteurId(eq(expediteurId), eq(status), any(Pageable.class));
    }

    @Test
    void getParcelsByClient_WithoutStatusFilter_ShouldReturnAllResults() throws Exception {
        // Given
        String expediteurId = "client-123";
        Page<ColisResponseDTO> page = new PageImpl<>(List.of(colisResponse), PageRequest.of(0, 10), 1);
        when(colisService.findByExpediteurId(eq(expediteurId), eq(null), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/colis/client/{expediteurId}", expediteurId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Parcels retrieved"));

        verify(colisService, times(1)).findByExpediteurId(eq(expediteurId), eq(null), any(Pageable.class));
    }

    @Test
    void getParcelsByDriver_WithStatusFilter_ShouldReturnFilteredResults() throws Exception {
        // Given
        String livreurId = "driver-456";
        Colis.ColisStatus status = Colis.ColisStatus.DELIVERED;
        Page<ColisResponseDTO> page = new PageImpl<>(List.of(colisResponse), PageRequest.of(0, 10), 1);
        when(colisService.findByLivreurId(eq(livreurId), eq(status), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/colis/driver/{livreurId}", livreurId)
                        .param("status", "DELIVERED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Parcels retrieved"))
                .andExpect(jsonPath("$.data.content[0].id").value("colis-123"));

        verify(colisService, times(1)).findByLivreurId(eq(livreurId), eq(status), any(Pageable.class));
    }

    @Test
    void getParcelsByDestinataire_ShouldReturnResults() throws Exception {
        // Given
        String destinataireId = "recipient-456";
        Page<ColisResponseDTO> page = new PageImpl<>(List.of(colisResponse), PageRequest.of(0, 10), 1);
        when(colisService.findByDestinataireId(eq(destinataireId), eq(null), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/colis/destinataire/{destinataireId}", destinataireId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Parcels for destinataire"))
                .andExpect(jsonPath("$.data.content[0].id").value("colis-123"));

        verify(colisService, times(1)).findByDestinataireId(eq(destinataireId), eq(null), any(Pageable.class));
    }

    @Test
    void updateStatus_WithValidRequest_ShouldReturnUpdatedColis() throws Exception {
        // Given
        String colisId = "colis-123";
        UpdateStatusRequest updateRequest = new UpdateStatusRequest();
        updateRequest.setStatut(Colis.ColisStatus.IN_TRANSIT);
        updateRequest.setCommentaire("En route to destination");
        updateRequest.setUtilisateurId("user-789");

        ColisResponseDTO updatedResponse = new ColisResponseDTO();
        updatedResponse.setId("colis-123");
        updatedResponse.setStatut(Colis.ColisStatus.IN_TRANSIT);

        when(colisService.updateStatus(eq(colisId), any(UpdateStatusRequest.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/colis/{id}/status", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Status updated to IN_TRANSIT"))
                .andExpect(jsonPath("$.data.statut").value("IN_TRANSIT"));

        verify(colisService, times(1)).updateStatus(eq(colisId), any(UpdateStatusRequest.class));
    }

    @Test
    void updateStatus_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        String colisId = "colis-123";
        UpdateStatusRequest invalidRequest = new UpdateStatusRequest();
        // Missing required fields: statut and utilisateurId

        // When & Then
        mockMvc.perform(put("/api/colis/{id}/status", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(colisService, never()).updateStatus(anyString(), any(UpdateStatusRequest.class));
    }

    @Test
    void getHistory_ShouldReturnHistory() throws Exception {
        // Given
        String colisId = "colis-123";
        HistoriqueLivraisonResponseDTO historyItem = new HistoriqueLivraisonResponseDTO();
        historyItem.setId("history-1");
        historyItem.setNouveauStatut(Colis.ColisStatus.IN_TRANSIT);

        Page<HistoriqueLivraisonResponseDTO> page = new PageImpl<>(List.of(historyItem), PageRequest.of(0, 10), 1);
        when(colisService.getHistory(eq(colisId), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/colis/{id}/history", colisId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Parcel history"))
                .andExpect(jsonPath("$.data.content[0].id").value("history-1"))
                .andExpect(jsonPath("$.data.content[0].nouveauStatut").value("IN_TRANSIT"));

        verify(colisService, times(1)).getHistory(eq(colisId), any(Pageable.class));
    }

    @Test
    void assignerLivreur_WithValidRequest_ShouldReturnUpdatedColis() throws Exception {
        // Given
        String colisId = "colis-123";
        AssignerLivreurRequestDTO assignRequest = new AssignerLivreurRequestDTO("driver-456");

        DriverResponseDTO driver = new DriverResponseDTO();
        driver.setId("driver-456");
        driver.setFirstName("Driver");
        driver.setLastName("One");

        ColisResponseDTO updatedResponse = new ColisResponseDTO();
        updatedResponse.setId("colis-123");
        updatedResponse.setLivreur(driver);

        when(colisService.assignerLivreur(eq(colisId), any(AssignerLivreurRequestDTO.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/colis/{id}/assign", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Driver assigned successfully"))
                .andExpect(jsonPath("$.data.livreur.id").value("driver-456"));

        verify(colisService, times(1)).assignerLivreur(eq(colisId), any(AssignerLivreurRequestDTO.class));
    }

    @Test
    void assignerLivreur_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        String colisId = "colis-123";
        AssignerLivreurRequestDTO invalidRequest = new AssignerLivreurRequestDTO(""); // Blank driver ID

        // When & Then
        mockMvc.perform(put("/api/colis/{id}/assign", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(colisService, never()).assignerLivreur(anyString(), any(AssignerLivreurRequestDTO.class));
    }

    @Test
    void syntheseByZone_ShouldReturnZoneStatistics() throws Exception {
        // Given
        SyntheseDTO<String> zoneStats = new SyntheseDTO<>("Zone Paris Center", 10L, 25.5);
        List<SyntheseDTO<String>> data = List.of(zoneStats);
        when(colisService.getSyntheseByZone()).thenReturn(data);

        // When & Then
        mockMvc.perform(get("/api/colis/synthese/zone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Synthèse par zone"))
                .andExpect(jsonPath("$.data[0].key").value("Zone Paris Center"))
                .andExpect(jsonPath("$.data[0].count").value(10))
                .andExpect(jsonPath("$.data[0].poidsTotal").value(25.5));

        verify(colisService, times(1)).getSyntheseByZone();
    }

    @Test
    void syntheseByStatut_ShouldReturnStatusStatistics() throws Exception {
        // Given
        SyntheseDTO<Colis.ColisStatus> statusStats = new SyntheseDTO<>(Colis.ColisStatus.DELIVERED, 5L, 12.5);
        List<SyntheseDTO<Colis.ColisStatus>> data = List.of(statusStats);
        when(colisService.getSyntheseByStatut()).thenReturn(data);

        // When & Then
        mockMvc.perform(get("/api/colis/synthese/statut"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Synthèse par statut"))
                .andExpect(jsonPath("$.data[0].key").value("DELIVERED"))
                .andExpect(jsonPath("$.data[0].count").value(5))
                .andExpect(jsonPath("$.data[0].poidsTotal").value(12.5));

        verify(colisService, times(1)).getSyntheseByStatut();
    }

    @Test
    void syntheseByPriorite_ShouldReturnPriorityStatistics() throws Exception {
        // Given
        SyntheseDTO<Colis.Priorite> priorityStats = new SyntheseDTO<>(Colis.Priorite.HIGH, 3L, 8.0);
        List<SyntheseDTO<Colis.Priorite>> data = List.of(priorityStats);
        when(colisService.getSyntheseByPriorite()).thenReturn(data);

        // When & Then
        mockMvc.perform(get("/api/colis/synthese/priorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Synthèse par priorité"))
                .andExpect(jsonPath("$.data[0].key").value("HIGH"))
                .andExpect(jsonPath("$.data[0].count").value(3))
                .andExpect(jsonPath("$.data[0].poidsTotal").value(8.0));

        verify(colisService, times(1)).getSyntheseByPriorite();
    }

    @Test
    void getProducts_ShouldReturnColisProducts() throws Exception {
        // Given
        String colisId = "colis-123";
        ColisProductResponseDTO product = new ColisProductResponseDTO();
        product.setProductId("product-1");
        product.setNom("Product One");
        product.setCategorie("Electronics");
        product.setPoids(1.5);
        product.setQuantite(2);
        product.setPrixUnitaire(99.99);
        product.setPrixTotal(199.98);

        Page<ColisProductResponseDTO> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(colisService.getProductsByColisId(eq(colisId), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/colis/{id}/products", colisId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].productId").value("product-1"))
                .andExpect(jsonPath("$.content[0].nom").value("Product One"))
                .andExpect(jsonPath("$.content[0].categorie").value("Electronics"))
                .andExpect(jsonPath("$.content[0].poids").value(1.5))
                .andExpect(jsonPath("$.content[0].quantite").value(2))
                .andExpect(jsonPath("$.content[0].prixUnitaire").value(99.99))
                .andExpect(jsonPath("$.content[0].prixTotal").value(199.98));

        verify(colisService, times(1)).getProductsByColisId(eq(colisId), any(Pageable.class));
    }

    @Test
    void updateColis_WithValidData_ShouldReturnUpdatedColis() throws Exception {
        // Given
        String colisId = "colis-123";
        when(colisService.update(eq(colisId), any(ColisRequestDTO.class))).thenReturn(colisResponse);

        // When & Then
        mockMvc.perform(put("/api/colis/{id}", colisId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource updated successfully"))
                .andExpect(jsonPath("$.data.id").value("colis-123"));

        verify(colisService, times(1)).update(eq(colisId), any(ColisRequestDTO.class));
    }

    @Test
    void deleteColis_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        String colisId = "colis-123";
        doNothing().when(colisService).deleteById(colisId);

        // When & Then
        mockMvc.perform(delete("/api/colis/{id}", colisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource deleted successfully"));

        verify(colisService, times(1)).deleteById(colisId);
    }

    @Test
    void getAllColis_ShouldReturnListOfColis() throws Exception {
        // Given
        List<ColisResponseDTO> colisList = List.of(colisResponse);
        when(colisService.findAll()).thenReturn(colisList);

        // When & Then
        mockMvc.perform(get("/api/colis/no-pagination"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value("colis-123"))
                .andExpect(jsonPath("$.data[0].reference").value("COLIS-001"));

        verify(colisService, times(1)).findAll();
    }

    @Test
    void getAllColisPaginated_ShouldReturnPaginatedResponse() throws Exception {
        // Given
        Page<ColisResponseDTO> page = new PageImpl<>(List.of(colisResponse), PageRequest.of(0, 10), 1);
        when(colisService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/colis")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "reference,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value("colis-123"))
                .andExpect(jsonPath("$.data.content[0].reference").value("COLIS-001"));

        verify(colisService, times(1)).findAll(any(Pageable.class), any());
    }

    // Direct method testing (without MockMvc)
    @Test
    void createColis_DirectMethodCall_ShouldReturnCreatedResponse() {
        // Given
        when(colisService.save(any(ColisRequestDTO.class))).thenReturn(colisResponse);

        // When
        ResponseEntity<ApiResponseDTO<ColisResponseDTO>> response =
                colisController.create(validColisRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource created successfully");
        assertThat(response.getBody().getData()).isEqualTo(colisResponse);

        verify(colisService, times(1)).save(any(ColisRequestDTO.class));
    }

    @Test
    void getParcelsByClient_DirectMethodCall_ShouldReturnParcels() {
        // Given
        String expediteurId = "client-123";
        Colis.ColisStatus status = Colis.ColisStatus.CREATED;
        Pageable pageable = PageRequest.of(0, 10);
        Page<ColisResponseDTO> page = new PageImpl<>(List.of(colisResponse), pageable, 1);
        when(colisService.findByExpediteurId(expediteurId, status, pageable)).thenReturn(page);

        // When
        ResponseEntity<ApiResponseDTO<Page<ColisResponseDTO>>> response =
                colisController.getParcelsByClient(expediteurId, status, pageable);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Parcels retrieved");
        assertThat(response.getBody().getData()).isEqualTo(page);

        verify(colisService, times(1)).findByExpediteurId(expediteurId, status, pageable);
    }

    @Test
    void syntheseByZone_DirectMethodCall_ShouldReturnStatistics() {
        // Given
        SyntheseDTO<String> zoneStats = new SyntheseDTO<>("Zone Paris", 5L, 15.0);
        List<SyntheseDTO<String>> data = List.of(zoneStats);
        when(colisService.getSyntheseByZone()).thenReturn(data);

        // When
        ResponseEntity<ApiResponseDTO<List<SyntheseDTO<String>>>> response =
                colisController.syntheseByZone();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Synthèse par zone");
        assertThat(response.getBody().getData()).isEqualTo(data);

        verify(colisService, times(1)).getSyntheseByZone();
    }

    @Test
    void createColis_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(colisService.save(any(ColisRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/colis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validColisRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please contact support."));

        verify(colisService, times(1)).save(any(ColisRequestDTO.class));
    }
}
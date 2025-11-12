package com.smartlogi.smartlogidms.masterdata.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.exception.GlobalExceptionHandler;
import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.service.ClientService;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne.PersonneRole;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClientService clientService;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientController clientController;

    private ObjectMapper objectMapper;

    private ClientRequestDTO validClientRequest;
    private ClientResponseDTO clientResponse;
    private ClientExpediteur clientEntity;

    @BeforeEach
    void setUp() {
        // Configure MockMvc with Pageable support and GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();

        // Setup test data for request
        validClientRequest = new ClientRequestDTO();
        validClientRequest.setFirstName("Company");
        validClientRequest.setLastName("Corp");
        validClientRequest.setEmail("contact@company.com");
        validClientRequest.setPhoneNumber("+33123456789");
        validClientRequest.setRue("123 Business Ave");
        validClientRequest.setVille("Paris");
        validClientRequest.setCodePostal("75001");

        // Setup test data for response
        clientResponse = new ClientResponseDTO();
        clientResponse.setId("client-123");
        clientResponse.setFirstName("Company");
        clientResponse.setLastName("Corp");
        clientResponse.setEmail("contact@company.com");
        clientResponse.setPhoneNumber("+33123456789");
        clientResponse.setRue("123 Business Ave");
        clientResponse.setVille("Paris");
        clientResponse.setCodePostal("75001");

        // Setup test data for entity
        clientEntity = new ClientExpediteur();
        clientEntity.setId("client-123");
        clientEntity.setFirstName("Company");
        clientEntity.setLastName("Corp");
        clientEntity.setEmail("contact@company.com");
        clientEntity.setPhoneNumber("+33123456789");
        clientEntity.setRole(PersonneRole.CLIENT);
        clientEntity.setAdresse(new Adresse("Paris", "123 Business Ave", "75001"));
    }

    @Test
    void createClient_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        when(clientService.save(any(ClientRequestDTO.class))).thenReturn(clientResponse);

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource created successfully"))
                .andExpect(jsonPath("$.data.id").value("client-123"))
                .andExpect(jsonPath("$.data.firstName").value("Company"))
                .andExpect(jsonPath("$.data.lastName").value("Corp"))
                .andExpect(jsonPath("$.data.email").value("contact@company.com"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+33123456789"))
                .andExpect(jsonPath("$.data.rue").value("123 Business Ave"))
                .andExpect(jsonPath("$.data.ville").value("Paris"))
                .andExpect(jsonPath("$.data.codePostal").value("75001"));

        verify(clientService, times(1)).save(any(ClientRequestDTO.class));
    }

    @Test
    void createClient_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        ClientRequestDTO invalidRequest = new ClientRequestDTO();
        invalidRequest.setFirstName(""); // Blank first name
        invalidRequest.setLastName(""); // Blank last name
        invalidRequest.setEmail("invalid-email"); // Invalid email format
        invalidRequest.setPhoneNumber(""); // Blank phone number
        invalidRequest.setVille(""); // Blank ville
        invalidRequest.setCodePostal(""); // Blank code postal

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(clientService, never()).save(any(ClientRequestDTO.class));
    }

    @Test
    void getClientById_WithValidId_ShouldReturnClient() throws Exception {
        // Given
        String clientId = "client-123";
        when(clientService.findById(clientId)).thenReturn(clientResponse);

        // When & Then
        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value("client-123"))
                .andExpect(jsonPath("$.data.firstName").value("Company"))
                .andExpect(jsonPath("$.data.lastName").value("Corp"))
                .andExpect(jsonPath("$.data.email").value("contact@company.com"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+33123456789"));

        verify(clientService, times(1)).findById(clientId);
    }

    @Test
    void getClientById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        when(clientService.findById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Client not found with id: " + nonExistentId));

        // When & Then
        mockMvc.perform(get("/api/clients/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Resource not found: Client not found with id: " + nonExistentId));

        verify(clientService, times(1)).findById(nonExistentId);
    }

    @Test
    void updateClient_WithValidData_ShouldReturnUpdatedClient() throws Exception {
        // Given
        String clientId = "client-123";
        when(clientService.update(eq(clientId), any(ClientRequestDTO.class))).thenReturn(clientResponse);

        // When & Then
        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource updated successfully"))
                .andExpect(jsonPath("$.data.id").value("client-123"))
                .andExpect(jsonPath("$.data.firstName").value("Company"))
                .andExpect(jsonPath("$.data.lastName").value("Corp"));

        verify(clientService, times(1)).update(eq(clientId), any(ClientRequestDTO.class));
    }

    @Test
    void updateClient_WithPartialData_ShouldWork() throws Exception {
        // Given - For UPDATE, only firstName and lastName validation should apply
        String clientId = "client-123";
        ClientRequestDTO partialUpdateRequest = new ClientRequestDTO();
        partialUpdateRequest.setFirstName("Company Updated");
        partialUpdateRequest.setLastName("Corp Updated");
        // Other fields can be null/empty for update

        ClientResponseDTO updatedResponse = new ClientResponseDTO();
        updatedResponse.setId("client-123");
        updatedResponse.setFirstName("Company Updated");
        updatedResponse.setLastName("Corp Updated");
        updatedResponse.setEmail("contact@company.com"); // unchanged
        updatedResponse.setPhoneNumber("+33123456789"); // unchanged

        when(clientService.update(eq(clientId), any(ClientRequestDTO.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.firstName").value("Company Updated"))
                .andExpect(jsonPath("$.data.lastName").value("Corp Updated"));

        verify(clientService, times(1)).update(eq(clientId), any(ClientRequestDTO.class));
    }

    @Test
    void updateClient_WithInvalidFieldLengths_ShouldReturnBadRequest() throws Exception {
        // Given
        String clientId = "client-123";
        ClientRequestDTO invalidRequest = new ClientRequestDTO();
        invalidRequest.setFirstName("A".repeat(51)); // Too long (max 50)
        invalidRequest.setLastName("B".repeat(51)); // Too long (max 50)
        invalidRequest.setEmail("valid@example.com"); // Valid email
        invalidRequest.setPhoneNumber("+33123456789"); // Valid phone
        invalidRequest.setVille("C".repeat(101)); // Too long (max 100)
        invalidRequest.setCodePostal("D".repeat(21)); // Too long (max 20)

        // When & Then - Size constraints apply to both Create and Update
        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(clientService, never()).update(anyString(), any(ClientRequestDTO.class));
    }

    @Test
    void deleteClient_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        String clientId = "client-123";
        doNothing().when(clientService).deleteById(clientId);

        // When & Then
        mockMvc.perform(delete("/api/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource deleted successfully"));

        verify(clientService, times(1)).deleteById(clientId);
    }

    @Test
    void getAllClients_ShouldReturnListOfClients() throws Exception {
        // Given
        List<ClientResponseDTO> clients = List.of(clientResponse);
        when(clientService.findAll()).thenReturn(clients);

        // When & Then
        mockMvc.perform(get("/api/clients/no-pagination"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value("client-123"))
                .andExpect(jsonPath("$.data[0].firstName").value("Company"))
                .andExpect(jsonPath("$.data[0].lastName").value("Corp"))
                .andExpect(jsonPath("$.data[0].email").value("contact@company.com"));

        verify(clientService, times(1)).findAll();
    }

    @Test
    void getAllClientsPaginated_ShouldReturnPaginatedResponse() throws Exception {
        // Given
        Page<ClientResponseDTO> page = new PageImpl<>(List.of(clientResponse), PageRequest.of(0, 10), 1);
        when(clientService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/clients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value("client-123"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("Company"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Corp"));

        verify(clientService, times(1)).findAll(any(Pageable.class), any());
    }

    @Test
    void searchClients_WithKeyword_ShouldReturnFilteredResults() throws Exception {
        // Given
        Page<ClientResponseDTO> page = new PageImpl<>(List.of(clientResponse), PageRequest.of(0, 10), 1);
        when(clientService.searchClients(eq("Company"), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/clients/search")
                        .param("q", "Company")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Selected Clients retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value("client-123"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("Company"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Corp"));

        verify(clientService, times(1)).searchClients(eq("Company"), any(Pageable.class));
    }

    @Test
    void searchClients_WithEmptyKeyword_ShouldReturnAllResults() throws Exception {
        // Given
        Page<ClientResponseDTO> page = new PageImpl<>(List.of(clientResponse), PageRequest.of(0, 10), 1);
        when(clientService.searchClients(eq(""), any(Pageable.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/clients/search")
                        .param("q", "")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Selected Clients retrieved successfully"));

        verify(clientService, times(1)).searchClients(eq(""), any(Pageable.class));
    }

    @Test
    void searchClients_WithNoKeyword_ShouldUseDefaultEmptyString() throws Exception {
        // Given
        Page<ClientResponseDTO> page = new PageImpl<>(List.of(clientResponse), PageRequest.of(0, 10), 1);
        when(clientService.searchClients(eq(""), any(Pageable.class))).thenReturn(page);

        // When & Then - No 'q' parameter provided, should use default value ""
        mockMvc.perform(get("/api/clients/search")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Selected Clients retrieved successfully"));

        verify(clientService, times(1)).searchClients(eq(""), any(Pageable.class));
    }

    @Test
    void searchClients_WithNoResults_ShouldReturnEmptyPage() throws Exception {
        // Given
        Page<ClientResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(clientService.searchClients(eq("Nonexistent"), any(Pageable.class))).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/clients/search")
                        .param("q", "Nonexistent")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Selected Clients retrieved successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());

        verify(clientService, times(1)).searchClients(eq("Nonexistent"), any(Pageable.class));
    }

    @Test
    void getAllClientsPaginated_WithEmptyResult_ShouldReturnEmptyPage() throws Exception {
        // Given
        Page<ClientResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(clientService.findAll(any(Pageable.class), any())).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/clients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());

        verify(clientService, times(1)).findAll(any(Pageable.class), any());
    }

    @Test
    void createClient_WithMaximumFieldLengths_ShouldWork() throws Exception {
        // Given
        ClientRequestDTO maxLengthRequest = new ClientRequestDTO();
        maxLengthRequest.setFirstName("A".repeat(50)); // Max length
        maxLengthRequest.setLastName("B".repeat(50)); // Max length
        maxLengthRequest.setEmail("test@example.com");
        maxLengthRequest.setPhoneNumber("+33123456789");
        maxLengthRequest.setRue("C".repeat(255)); // Max length
        maxLengthRequest.setVille("D".repeat(100)); // Max length
        maxLengthRequest.setCodePostal("E".repeat(20)); // Max length

        when(clientService.save(any(ClientRequestDTO.class))).thenReturn(clientResponse);

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maxLengthRequest)))
                .andExpect(status().isCreated());

        verify(clientService, times(1)).save(any(ClientRequestDTO.class));
    }

    @Test
    void createClient_WithNullEmail_ShouldWork() throws Exception {
        // Given - Email is optional (nullable in entity) but validation requires email format when provided
        ClientRequestDTO requestWithNullEmail = new ClientRequestDTO();
        requestWithNullEmail.setFirstName("Company");
        requestWithNullEmail.setLastName("Corp");
        requestWithNullEmail.setEmail(null); // Null email should be allowed
        requestWithNullEmail.setPhoneNumber("+33123456789");
        requestWithNullEmail.setVille("Paris");
        requestWithNullEmail.setCodePostal("75001");

        ClientResponseDTO responseWithNullEmail = new ClientResponseDTO();
        responseWithNullEmail.setId("client-123");
        responseWithNullEmail.setFirstName("Company");
        responseWithNullEmail.setLastName("Corp");
        responseWithNullEmail.setEmail(null);
        responseWithNullEmail.setPhoneNumber("+33123456789");
        responseWithNullEmail.setVille("Paris");
        responseWithNullEmail.setCodePostal("75001");

        when(clientService.save(any(ClientRequestDTO.class))).thenReturn(responseWithNullEmail);

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithNullEmail)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").doesNotExist());

        verify(clientService, times(1)).save(any(ClientRequestDTO.class));
    }

    // Direct method testing (without MockMvc)
    @Test
    void createClient_DirectMethodCall_ShouldReturnCreatedResponse() {
        // Given
        when(clientService.save(any(ClientRequestDTO.class))).thenReturn(clientResponse);

        // When
        ResponseEntity<ApiResponseDTO<ClientResponseDTO>> response =
                clientController.create(validClientRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource created successfully");
        assertThat(response.getBody().getData()).isEqualTo(clientResponse);

        verify(clientService, times(1)).save(any(ClientRequestDTO.class));
    }

    @Test
    void getClientById_DirectMethodCall_ShouldReturnClient() {
        // Given
        String clientId = "client-123";
        when(clientService.findById(clientId)).thenReturn(clientResponse);

        // When
        ResponseEntity<ApiResponseDTO<ClientResponseDTO>> response =
                clientController.getById(clientId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource retrieved successfully");
        assertThat(response.getBody().getData()).isEqualTo(clientResponse);

        verify(clientService, times(1)).findById(clientId);
    }

    @Test
    void updateClient_DirectMethodCall_ShouldReturnUpdatedClient() {
        // Given
        String clientId = "client-123";
        when(clientService.update(eq(clientId), any(ClientRequestDTO.class))).thenReturn(clientResponse);

        // When
        ResponseEntity<ApiResponseDTO<ClientResponseDTO>> response =
                clientController.update(clientId, validClientRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource updated successfully");
        assertThat(response.getBody().getData()).isEqualTo(clientResponse);

        verify(clientService, times(1)).update(eq(clientId), any(ClientRequestDTO.class));
    }

    @Test
    void deleteClient_DirectMethodCall_ShouldReturnSuccess() {
        // Given
        String clientId = "client-123";
        doNothing().when(clientService).deleteById(clientId);

        // When
        ResponseEntity<ApiResponseDTO<Void>> response =
                clientController.delete(clientId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource deleted successfully");

        verify(clientService, times(1)).deleteById(clientId);
    }

    @Test
    void searchClients_DirectMethodCall_ShouldReturnSearchResults() {
        // Given
        String keyword = "Company";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClientResponseDTO> page = new PageImpl<>(List.of(clientResponse), pageable, 1);
        when(clientService.searchClients(keyword, pageable)).thenReturn(page);

        // When
        ResponseEntity<ApiResponseDTO<Page<ClientResponseDTO>>> response =
                clientController.search(keyword, pageable);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Selected Clients retrieved successfully");
        assertThat(response.getBody().getData()).isEqualTo(page);

        verify(clientService, times(1)).searchClients(keyword, pageable);
    }

    @Test
    void createClient_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(clientService.save(any(ClientRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClientRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please contact support."));

        verify(clientService, times(1)).save(any(ClientRequestDTO.class));
    }
}
package com.smartlogi.smartlogidms.masterdata.recipient.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import com.smartlogi.smartlogidms.common.exception.GlobalExceptionHandler;
import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.recipient.service.RecipientService;
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
class RecipientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecipientService recipientService;

    @Mock
    private RecipientMapper recipientMapper;

    @InjectMocks
    private RecipientController recipientController;

    private ObjectMapper objectMapper;

    private RecipientRequestDTO validRecipientRequest;
    private RecipientResponseDTO recipientResponse;
    private Recipient recipientEntity;

    @BeforeEach
    void setUp() {
        // Configure MockMvc with Pageable support and GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(recipientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();

        // Setup test data for request
        validRecipientRequest = new RecipientRequestDTO();
        validRecipientRequest.setFirstName("John");
        validRecipientRequest.setLastName("Doe");
        validRecipientRequest.setEmail("john.doe@example.com");
        validRecipientRequest.setPhoneNumber("+33123456789");
        validRecipientRequest.setRue("123 Main Street");
        validRecipientRequest.setVille("Paris");
        validRecipientRequest.setCodePostal("75001");

        // Setup test data for response
        recipientResponse = new RecipientResponseDTO();
        recipientResponse.setId("recipient-123");
        recipientResponse.setFirstName("John");
        recipientResponse.setLastName("Doe");
        recipientResponse.setEmail("john.doe@example.com");
        recipientResponse.setPhoneNumber("+33123456789");
        recipientResponse.setRue("123 Main Street");
        recipientResponse.setVille("Paris");
        recipientResponse.setCodePostal("75001");

        // Setup test data for entity
        recipientEntity = new Recipient();
        recipientEntity.setId("recipient-123");
        recipientEntity.setFirstName("John");
        recipientEntity.setLastName("Doe");
        recipientEntity.setEmail("john.doe@example.com");
        recipientEntity.setPhoneNumber("+33123456789");
        recipientEntity.setRole(PersonneRole.CLIENT);
        recipientEntity.setAdresse(new Adresse("Paris", "123 Main Street", "75001"));
    }

    @Test
    void createRecipient_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        when(recipientService.save(any(RecipientRequestDTO.class))).thenReturn(recipientResponse);

        // When & Then
        mockMvc.perform(post("/api/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecipientRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource created successfully"))
                .andExpect(jsonPath("$.data.id").value("recipient-123"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+33123456789"))
                .andExpect(jsonPath("$.data.rue").value("123 Main Street"))
                .andExpect(jsonPath("$.data.ville").value("Paris"))
                .andExpect(jsonPath("$.data.codePostal").value("75001"));

        verify(recipientService, times(1)).save(any(RecipientRequestDTO.class));
    }

    @Test
    void createRecipient_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        RecipientRequestDTO invalidRequest = new RecipientRequestDTO();
        invalidRequest.setFirstName(""); // Blank first name
        invalidRequest.setLastName(""); // Blank last name
        invalidRequest.setEmail("invalid-email"); // Invalid email format
        invalidRequest.setPhoneNumber(""); // Blank phone number
        invalidRequest.setVille(""); // Blank ville
        invalidRequest.setCodePostal(""); // Blank code postal

        // When & Then
        mockMvc.perform(post("/api/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(recipientService, never()).save(any(RecipientRequestDTO.class));
    }

    @Test
    void createRecipient_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        // Given - For CREATE, all @NotBlank fields with Create group are required
        RecipientRequestDTO missingFieldsRequest = new RecipientRequestDTO();
        // Missing firstName, lastName, phoneNumber, ville, codePostal

        // When & Then
        mockMvc.perform(post("/api/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missingFieldsRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(recipientService, never()).save(any(RecipientRequestDTO.class));
    }

    @Test
    void getRecipientById_WithValidId_ShouldReturnRecipient() throws Exception {
        // Given
        String recipientId = "recipient-123";
        when(recipientService.findById(recipientId)).thenReturn(recipientResponse);

        // When & Then
        mockMvc.perform(get("/api/recipients/{id}", recipientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value("recipient-123"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+33123456789"))
                .andExpect(jsonPath("$.data.rue").value("123 Main Street"))
                .andExpect(jsonPath("$.data.ville").value("Paris"))
                .andExpect(jsonPath("$.data.codePostal").value("75001"));

        verify(recipientService, times(1)).findById(recipientId);
    }

    @Test
    void getRecipientById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        when(recipientService.findById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Recipient not found with id: " + nonExistentId));

        // When & Then
        mockMvc.perform(get("/api/recipients/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Resource not found: Recipient not found with id: " + nonExistentId));

        verify(recipientService, times(1)).findById(nonExistentId);
    }

    @Test
    void updateRecipient_WithValidData_ShouldReturnUpdatedRecipient() throws Exception {
        // Given
        String recipientId = "recipient-123";
        when(recipientService.update(eq(recipientId), any(RecipientRequestDTO.class))).thenReturn(recipientResponse);

        // When & Then
        mockMvc.perform(put("/api/recipients/{id}", recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecipientRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource updated successfully"))
                .andExpect(jsonPath("$.data.id").value("recipient-123"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"));

        verify(recipientService, times(1)).update(eq(recipientId), any(RecipientRequestDTO.class));
    }

    @Test
    void updateRecipient_WithPartialData_ShouldWork() throws Exception {
        // Given - For UPDATE, only firstName and lastName validation should apply
        String recipientId = "recipient-123";
        RecipientRequestDTO partialUpdateRequest = new RecipientRequestDTO();
        partialUpdateRequest.setFirstName("John Updated");
        partialUpdateRequest.setLastName("Doe Updated");
        // Other fields can be null/empty for update

        RecipientResponseDTO updatedResponse = new RecipientResponseDTO();
        updatedResponse.setId("recipient-123");
        updatedResponse.setFirstName("John Updated");
        updatedResponse.setLastName("Doe Updated");
        updatedResponse.setEmail("john.doe@example.com"); // unchanged
        updatedResponse.setPhoneNumber("+33123456789"); // unchanged

        when(recipientService.update(eq(recipientId), any(RecipientRequestDTO.class))).thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/recipients/{id}", recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.firstName").value("John Updated"))
                .andExpect(jsonPath("$.data.lastName").value("Doe Updated"));

        verify(recipientService, times(1)).update(eq(recipientId), any(RecipientRequestDTO.class));
    }

    @Test
    void updateRecipient_WithInvalidFieldLengths_ShouldReturnBadRequest() throws Exception {
        // Given
        String recipientId = "recipient-123";
        RecipientRequestDTO invalidRequest = new RecipientRequestDTO();
        invalidRequest.setFirstName("A".repeat(51)); // Too long (max 50)
        invalidRequest.setLastName("B".repeat(51)); // Too long (max 50)
        invalidRequest.setEmail("valid@example.com"); // Valid email
        invalidRequest.setPhoneNumber("+33123456789"); // Valid phone
        invalidRequest.setVille("C".repeat(101)); // Too long (max 100)
        invalidRequest.setCodePostal("D".repeat(21)); // Too long (max 20)

        // When & Then - Size constraints apply to both Create and Update
        mockMvc.perform(put("/api/recipients/{id}", recipientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(recipientService, never()).update(anyString(), any(RecipientRequestDTO.class));
    }

    @Test
    void deleteRecipient_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        String recipientId = "recipient-123";
        doNothing().when(recipientService).deleteById(recipientId);

        // When & Then
        mockMvc.perform(delete("/api/recipients/{id}", recipientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource deleted successfully"));

        verify(recipientService, times(1)).deleteById(recipientId);
    }

    @Test
    void getAllRecipients_ShouldReturnListOfRecipients() throws Exception {
        // Given
        List<RecipientResponseDTO> recipients = List.of(recipientResponse);
        when(recipientService.findAll()).thenReturn(recipients);

        // When & Then
        mockMvc.perform(get("/api/recipients/no-pagination"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value("recipient-123"))
                .andExpect(jsonPath("$.data[0].firstName").value("John"))
                .andExpect(jsonPath("$.data[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data[0].email").value("john.doe@example.com"));

        verify(recipientService, times(1)).findAll();
    }

    @Test
    void getAllRecipientsPaginated_ShouldReturnPaginatedResponse() throws Exception {
        // Given
        Page<RecipientResponseDTO> page = new PageImpl<>(List.of(recipientResponse), PageRequest.of(0, 10), 1);
        when(recipientService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/recipients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "lastName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value("recipient-123"))
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.data.content[0].lastName").value("Doe"));

        verify(recipientService, times(1)).findAll(any(Pageable.class), any());
    }

    @Test
    void getAllRecipientsPaginated_WithFilters_ShouldReturnFilteredResponse() throws Exception {
        // Given
        Page<RecipientResponseDTO> page = new PageImpl<>(List.of(recipientResponse), PageRequest.of(0, 10), 1);
        when(recipientService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - Test with filter parameters
        mockMvc.perform(get("/api/recipients")
                        .param("page", "0")
                        .param("size", "10")
                        .param("lastName", "Doe")
                        .param("ville", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"));

        verify(recipientService, times(1)).findAll(any(Pageable.class), any());
    }

    @Test
    void getAllRecipientsPaginated_WithEmptyResult_ShouldReturnEmptyPage() throws Exception {
        // Given
        Page<RecipientResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(recipientService.findAll(any(Pageable.class), any())).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/recipients")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());

        verify(recipientService, times(1)).findAll(any(Pageable.class), any());
    }

    @Test
    void createRecipient_WithMaximumFieldLengths_ShouldWork() throws Exception {
        // Given
        RecipientRequestDTO maxLengthRequest = new RecipientRequestDTO();
        maxLengthRequest.setFirstName("A".repeat(50)); // Max length
        maxLengthRequest.setLastName("B".repeat(50)); // Max length
        maxLengthRequest.setEmail("test@example.com");
        maxLengthRequest.setPhoneNumber("+33123456789");
        maxLengthRequest.setRue("C".repeat(255)); // Max length
        maxLengthRequest.setVille("D".repeat(100)); // Max length
        maxLengthRequest.setCodePostal("E".repeat(20)); // Max length

        when(recipientService.save(any(RecipientRequestDTO.class))).thenReturn(recipientResponse);

        // When & Then
        mockMvc.perform(post("/api/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maxLengthRequest)))
                .andExpect(status().isCreated());

        verify(recipientService, times(1)).save(any(RecipientRequestDTO.class));
    }

    @Test
    void createRecipient_WithNullEmail_ShouldWork() throws Exception {
        // Given - Email is optional (nullable in entity) but validation requires email format when provided
        RecipientRequestDTO requestWithNullEmail = new RecipientRequestDTO();
        requestWithNullEmail.setFirstName("John");
        requestWithNullEmail.setLastName("Doe");
        requestWithNullEmail.setEmail(null); // Null email should be allowed
        requestWithNullEmail.setPhoneNumber("+33123456789");
        requestWithNullEmail.setVille("Paris");
        requestWithNullEmail.setCodePostal("75001");

        RecipientResponseDTO responseWithNullEmail = new RecipientResponseDTO();
        responseWithNullEmail.setId("recipient-123");
        responseWithNullEmail.setFirstName("John");
        responseWithNullEmail.setLastName("Doe");
        responseWithNullEmail.setEmail(null);
        responseWithNullEmail.setPhoneNumber("+33123456789");
        responseWithNullEmail.setVille("Paris");
        responseWithNullEmail.setCodePostal("75001");

        when(recipientService.save(any(RecipientRequestDTO.class))).thenReturn(responseWithNullEmail);

        // When & Then
        mockMvc.perform(post("/api/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithNullEmail)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").doesNotExist());

        verify(recipientService, times(1)).save(any(RecipientRequestDTO.class));
    }

    // Direct method testing (without MockMvc)
    @Test
    void createRecipient_DirectMethodCall_ShouldReturnCreatedResponse() {
        // Given
        when(recipientService.save(any(RecipientRequestDTO.class))).thenReturn(recipientResponse);

        // When
        ResponseEntity<ApiResponseDTO<RecipientResponseDTO>> response =
                recipientController.create(validRecipientRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource created successfully");
        assertThat(response.getBody().getData()).isEqualTo(recipientResponse);

        verify(recipientService, times(1)).save(any(RecipientRequestDTO.class));
    }

    @Test
    void getRecipientById_DirectMethodCall_ShouldReturnRecipient() {
        // Given
        String recipientId = "recipient-123";
        when(recipientService.findById(recipientId)).thenReturn(recipientResponse);

        // When
        ResponseEntity<ApiResponseDTO<RecipientResponseDTO>> response =
                recipientController.getById(recipientId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource retrieved successfully");
        assertThat(response.getBody().getData()).isEqualTo(recipientResponse);

        verify(recipientService, times(1)).findById(recipientId);
    }

    @Test
    void updateRecipient_DirectMethodCall_ShouldReturnUpdatedRecipient() {
        // Given
        String recipientId = "recipient-123";
        when(recipientService.update(eq(recipientId), any(RecipientRequestDTO.class))).thenReturn(recipientResponse);

        // When
        ResponseEntity<ApiResponseDTO<RecipientResponseDTO>> response =
                recipientController.update(recipientId, validRecipientRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource updated successfully");
        assertThat(response.getBody().getData()).isEqualTo(recipientResponse);

        verify(recipientService, times(1)).update(eq(recipientId), any(RecipientRequestDTO.class));
    }

    @Test
    void deleteRecipient_DirectMethodCall_ShouldReturnSuccess() {
        // Given
        String recipientId = "recipient-123";
        doNothing().when(recipientService).deleteById(recipientId);

        // When
        ResponseEntity<ApiResponseDTO<Void>> response =
                recipientController.delete(recipientId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource deleted successfully");

        verify(recipientService, times(1)).deleteById(recipientId);
    }

    @Test
    void createRecipient_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(recipientService.save(any(RecipientRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRecipientRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please contact support."));

        verify(recipientService, times(1)).save(any(RecipientRequestDTO.class));
    }
}
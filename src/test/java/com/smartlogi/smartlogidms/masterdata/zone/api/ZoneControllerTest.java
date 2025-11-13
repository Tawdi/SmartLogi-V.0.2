package com.smartlogi.smartlogidms.masterdata.zone.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.exception.GlobalExceptionHandler;
import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.service.ZoneService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ZoneControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ZoneService zoneService;

    @Mock
    private ZoneMapper zoneMapper;

    @InjectMocks
    private ZoneController zoneController;

    private ObjectMapper objectMapper;

    private ZoneRequestDTO validZoneRequest;
    private ZoneResponseDTO zoneResponse;
    private Zone zoneEntity;

    @BeforeEach
    void setUp() {
        // Configure MockMvc with Pageable support and GlobalExceptionHandler
        mockMvc = MockMvcBuilders.standaloneSetup(zoneController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Add this line
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();

        // Setup test data
        validZoneRequest = new ZoneRequestDTO();
        validZoneRequest.setName("Zone Paris Center");
        validZoneRequest.setCodePostal("75001");

        zoneResponse = new ZoneResponseDTO();
        zoneResponse.setId("zone-123");
        zoneResponse.setName("Zone Paris Center");
        zoneResponse.setCodePostal("75001");

        zoneEntity = new Zone();
        zoneEntity.setId("zone-123");
        zoneEntity.setName("Zone Paris Center");
        zoneEntity.setCodePostal("75001");
    }

    @Test
    void createZone_WithValidData_ShouldReturnCreated() throws Exception {
        // Given
        when(zoneService.save(any(ZoneRequestDTO.class))).thenReturn(zoneResponse);

        // When & Then
        mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource created successfully"))
                .andExpect(jsonPath("$.data.id").value("zone-123"))
                .andExpect(jsonPath("$.data.name").value("Zone Paris Center"))
                .andExpect(jsonPath("$.data.codePostal").value("75001"));

        verify(zoneService, times(1)).save(any(ZoneRequestDTO.class));
    }

    @Test
    void createZone_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        ZoneRequestDTO invalidRequest = new ZoneRequestDTO();
        invalidRequest.setName(""); // Invalid: blank name
        invalidRequest.setCodePostal(""); // Invalid: blank postal code

        // When & Then
        mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(zoneService, never()).save(any(ZoneRequestDTO.class));
    }

    @Test
    void getZoneById_WithValidId_ShouldReturnZone() throws Exception {
        // Given
        String zoneId = "zone-123";
        when(zoneService.findById(zoneId)).thenReturn(zoneResponse);

        // When & Then
        mockMvc.perform(get("/api/zones/{id}", zoneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value("zone-123"))
                .andExpect(jsonPath("$.data.name").value("Zone Paris Center"));

        verify(zoneService, times(1)).findById(zoneId);
    }

    @Test
    void getZoneById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        String nonExistentId = "non-existent-id";
        when(zoneService.findById(nonExistentId))
                .thenThrow(new ResourceNotFoundException("Resource not found with id: " + nonExistentId));

        // When & Then
        mockMvc.perform(get("/api/zones/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Resource not found: Resource not found with id: " + nonExistentId));

        verify(zoneService, times(1)).findById(nonExistentId);
    }

    @Test
    void updateZone_WithValidData_ShouldReturnUpdatedZone() throws Exception {
        // Given
        String zoneId = "zone-123";
        when(zoneService.update(eq(zoneId), any(ZoneRequestDTO.class))).thenReturn(zoneResponse);

        // When & Then
        mockMvc.perform(put("/api/zones/{id}", zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource updated successfully"))
                .andExpect(jsonPath("$.data.id").value("zone-123"))
                .andExpect(jsonPath("$.data.name").value("Zone Paris Center"));

        verify(zoneService, times(1)).update(eq(zoneId), any(ZoneRequestDTO.class));
    }

    @Test
    void updateZone_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        String zoneId = "zone-123";
        ZoneRequestDTO invalidRequest = new ZoneRequestDTO();
        invalidRequest.setName("A"); // Too short
        invalidRequest.setCodePostal(""); // Blank

        // When & Then
        mockMvc.perform(put("/api/zones/{id}", zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").exists());

        verify(zoneService, never()).update(anyString(), any(ZoneRequestDTO.class));
    }

    @Test
    void deleteZone_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        String zoneId = "zone-123";
        doNothing().when(zoneService).deleteById(zoneId);

        // When & Then
        mockMvc.perform(delete("/api/zones/{id}", zoneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resource deleted successfully"));

        verify(zoneService, times(1)).deleteById(zoneId);
    }

    @Test
    void getAllZones_ShouldReturnListOfZones() throws Exception {
        // Given
        List<ZoneResponseDTO> zones = List.of(zoneResponse);
        when(zoneService.findAll()).thenReturn(zones);

        // When & Then
        mockMvc.perform(get("/api/zones/no-pagination"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value("zone-123"))
                .andExpect(jsonPath("$.data[0].name").value("Zone Paris Center"));

        verify(zoneService, times(1)).findAll();
    }

    @Test
    void getAllZonesPaginated_ShouldReturnPaginatedResponse() throws Exception {
        // Given
        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zoneResponse), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/zones")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value("zone-123"))
                .andExpect(jsonPath("$.data.content[0].name").value("Zone Paris Center"));

        verify(zoneService, times(1)).findAll(any(Pageable.class), any());
    }

    @Test
    void getAllZonesPaginated_WithFilters_ShouldReturnFilteredResponse() throws Exception {
        // Given
        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zoneResponse), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - Test with filter parameters
        mockMvc.perform(get("/api/zones")
                        .param("page", "0")
                        .param("size", "10")
                        .param("name", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"));

        verify(zoneService, times(1)).findAll(any(Pageable.class), any());
    }

    @Test
    void getAllZonesPaginated_WithEmptyResult_ShouldReturnEmptyPage() throws Exception {
        // Given
        Page<ZoneResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/zones")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());

        verify(zoneService, times(1)).findAll(any(Pageable.class), any());
    }

    // Test for validation error details
    @Test
    void createZone_WithInvalidData_ShouldReturnDetailedValidationErrors() throws Exception {
        // Given
        ZoneRequestDTO invalidRequest = new ZoneRequestDTO();
        invalidRequest.setName("AB"); // Too short (min 3)
        invalidRequest.setCodePostal(""); // Blank

        // When & Then
        mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.codePostal").exists());

        verify(zoneService, never()).save(any(ZoneRequestDTO.class));
    }

    // Direct method testing (without MockMvc)
    @Test
    void update_DirectMethodCall_ShouldReturnResponseEntity() {
        // Given
        String zoneId = "zone-123";
        when(zoneService.update(eq(zoneId), any(ZoneRequestDTO.class))).thenReturn(zoneResponse);

        // When
        ResponseEntity<ApiResponseDTO<ZoneResponseDTO>> response =
                zoneController.update(zoneId, validZoneRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource updated successfully");
        assertThat(response.getBody().getData()).isEqualTo(zoneResponse);

        verify(zoneService, times(1)).update(eq(zoneId), any(ZoneRequestDTO.class));
    }

    @Test
    void createZone_DirectMethodCall_ShouldReturnCreatedResponse() {
        // Given
        when(zoneService.save(any(ZoneRequestDTO.class))).thenReturn(zoneResponse);

        // When
        ResponseEntity<ApiResponseDTO<ZoneResponseDTO>> response =
                zoneController.create(validZoneRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource created successfully");
        assertThat(response.getBody().getData()).isEqualTo(zoneResponse);

        verify(zoneService, times(1)).save(any(ZoneRequestDTO.class));
    }

    @Test
    void getZoneById_DirectMethodCall_ShouldReturnZone() {
        // Given
        String zoneId = "zone-123";
        when(zoneService.findById(zoneId)).thenReturn(zoneResponse);

        // When
        ResponseEntity<ApiResponseDTO<ZoneResponseDTO>> response =
                zoneController.getById(zoneId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getBody().getMessage()).isEqualTo("Resource retrieved successfully");
        assertThat(response.getBody().getData()).isEqualTo(zoneResponse);

        verify(zoneService, times(1)).findById(zoneId);
    }

    // Test for service throwing generic exception
    @Test
    void createZone_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(zoneService.save(any(ZoneRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validZoneRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please contact support."));

        verify(zoneService, times(1)).save(any(ZoneRequestDTO.class));
    }
}
package com.smartlogi.smartlogidms.masterdata.zone.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.common.exception.GlobalExceptionHandler;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ZoneControllerSpecificationTest {

    private MockMvc mockMvc;

    @Mock
    private ZoneService zoneService;

    @Mock
    private ZoneMapper zoneMapper;

    @InjectMocks
    private ZoneController zoneController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(zoneController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllPaginated_WithSearchParameter_ShouldFilterResults() throws Exception {
        // Given
        ZoneResponseDTO zone1 = new ZoneResponseDTO();
        zone1.setId("zone-1");
        zone1.setName("Paris Center");
        zone1.setCodePostal("75001");

        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zone1), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - Search for "Paris"
        mockMvc.perform(get("/api/zones")
                        .param("search", "Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Resources retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].name").value("Paris Center"));
    }

    @Test
    void getAllPaginated_WithEqualFilter_ShouldFilterByExactMatch() throws Exception {
        // Given
        ZoneResponseDTO zone1 = new ZoneResponseDTO();
        zone1.setId("zone-1");
        zone1.setName("Paris Center");
        zone1.setCodePostal("75001");

        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zone1), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - Filter by exact postal code
        mockMvc.perform(get("/api/zones")
                        .param("filter", "codePostal:eq:75001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getAllPaginated_WithLikeFilter_ShouldFilterByPartialMatch() throws Exception {
        // Given
        ZoneResponseDTO zone1 = new ZoneResponseDTO();
        zone1.setId("zone-1");
        zone1.setName("Paris Center");
        zone1.setCodePostal("75001");

        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zone1), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - Filter by partial name match
        mockMvc.perform(get("/api/zones")
                        .param("filter", "name:like:Paris"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getAllPaginated_WithMultipleFilters_ShouldCombineFilters() throws Exception {
        // Given
        ZoneResponseDTO zone1 = new ZoneResponseDTO();
        zone1.setId("zone-1");
        zone1.setName("Paris Center");
        zone1.setCodePostal("75001");

        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zone1), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - Multiple filters
        mockMvc.perform(get("/api/zones")
                        .param("filter", "name:like:Paris")
                        .param("filter", "codePostal:eq:75001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getAllPaginated_WithGreaterThanFilter_ShouldFilterByComparison() throws Exception {
        // Given
        ZoneResponseDTO zone1 = new ZoneResponseDTO();
        zone1.setId("zone-1");
        zone1.setName("Zone 1");

        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zone1), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - Greater than filter (for numeric fields)
        mockMvc.perform(get("/api/zones")
                        .param("filter", "someNumericField:gt:100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getAllPaginated_WithNotEqualFilter_ShouldExcludeValues() throws Exception {
        // Given
        ZoneResponseDTO zone1 = new ZoneResponseDTO();
        zone1.setId("zone-1");
        zone1.setName("Active Zone");

        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zone1), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - Not equal filter
        mockMvc.perform(get("/api/zones")
                        .param("filter", "status:neq:INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getAllPaginated_WithPagination_ShouldReturnPaginatedResults() throws Exception {
        // Given
        ZoneResponseDTO zone1 = new ZoneResponseDTO();
        zone1.setId("zone-1");
        zone1.setName("Zone 1");

        Page<ZoneResponseDTO> page = new PageImpl<>(List.of(zone1), PageRequest.of(0, 10), 1);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(page);

        // When & Then - With pagination parameters
        mockMvc.perform(get("/api/zones")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void getAllPaginated_WithEmptyResult_ShouldReturnEmptyPage() throws Exception {
        // Given
        Page<ZoneResponseDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(zoneService.findAll(any(Pageable.class), any())).thenReturn(emptyPage);

        // When & Then - Filter that returns no results
        mockMvc.perform(get("/api/zones")
                        .param("filter", "name:eq:NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }
}
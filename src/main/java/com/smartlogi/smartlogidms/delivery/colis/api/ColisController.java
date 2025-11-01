package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.common.api.controller.StringBaseController;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.colis.service.ColisService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colis")
@Tag(name = "Colis", description = "Colis management APIs")
public class ColisController extends StringBaseController<Colis, ColisRequestDTO, ColisResponseDTO> {

    private final ColisService colisService;
    private final ColisMapper colisMapper;

    public ColisController(ColisService colisService, ColisMapper colisMapper) {
        super(colisService, colisMapper);
        this.colisService = colisService;
        this.colisMapper = colisMapper;
    }

    @GetMapping("/client/{expediteurId}")
    @Operation(summary = "Get parcels for a client (expediteur)", description = "Filter by status (optional); null = all, DELIVERED = delivered, other = in progress")
    public ResponseEntity<ApiResponseDTO<Page<ColisResponseDTO>>> getParcelsByClient(
            @PathVariable String expediteurId,
            @Parameter(description = "Status filter (CREATED, DELIVERED, etc.; null for all)") @RequestParam(required = false) Colis.ColisStatus status,
            @Parameter(description = "Pagination (page, size, sort)") Pageable pageable) {

        Page<ColisResponseDTO> parcels = colisService.findByExpediteurId(expediteurId, status, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success("Parcels retrieved", parcels));
    }
}

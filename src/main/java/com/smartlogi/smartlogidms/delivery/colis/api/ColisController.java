package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.common.api.controller.StringBaseController;
import com.smartlogi.smartlogidms.common.api.dto.ApiResponseDTO;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.colis.service.ColisService;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonResponseDTO;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/driver/{livreurId}")
    @Operation(summary = "Get parcels for a driver (livreur)", description = "Filter by status (optional); null = all, DELIVERED = delivered, other = in progress")
    public ResponseEntity<ApiResponseDTO<Page<ColisResponseDTO>>> getParcelsByDriver(
            @PathVariable String livreurId,
            @Parameter(description = "Status filter (CREATED, DELIVERED, etc.; null for all)") @RequestParam(required = false) Colis.ColisStatus status,
            @Parameter(description = "Pagination (page, size, sort)") Pageable pageable) {

        Page<ColisResponseDTO> parcels = colisService.findByLivreurId(livreurId, status, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success("Parcels retrieved", parcels));
    }


    @GetMapping("/destinataire/{destinataireId}")
    @Operation(summary = "Get parcels for a destinataire", description = "Filter by status (optional)")
    public ResponseEntity<ApiResponseDTO<Page<ColisResponseDTO>>> getParcelsByDestinataire(
            @PathVariable String destinataireId,
            @Parameter(description = "Status filter (null for all)") @RequestParam(required = false) Colis.ColisStatus status,
            @Parameter(description = "Pagination") Pageable pageable) {

        Page<ColisResponseDTO> parcels = colisService.findByDestinataireId(destinataireId, status, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success("Parcels for destinataire", parcels));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update parcel status (livreur only)")
    public ResponseEntity<ApiResponseDTO<ColisResponseDTO>> updateStatus(
            @PathVariable String id,
            @RequestBody @Valid UpdateStatusRequest request) {

        ColisResponseDTO updated = colisService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponseDTO.success("Status updated to " + request.getStatut(), updated));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get history for a parcel")
    public ResponseEntity<ApiResponseDTO<Page<HistoriqueLivraisonResponseDTO>>> getHistory(
            @PathVariable String id,
            Pageable pageable) {

        Page<HistoriqueLivraisonResponseDTO> history = colisService.getHistory(id, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success("Parcel history", history));
    }


    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign a driver to a package", description = "Manager only")
    public ResponseEntity<ApiResponseDTO<ColisResponseDTO>> assignerLivreur(
            @PathVariable String id,
            @Valid @RequestBody AssignerLivreurRequestDTO request
    ) {
        ColisResponseDTO response = colisService.assignerLivreur(id, request);
        return ResponseEntity.ok(ApiResponseDTO.success("Driver assigned successfully", response));
    }


    @GetMapping("/synthese/zone")
    @Operation(summary = "Synthèse par zone", description = "Nombre et poids total de colis par zone")
    public ResponseEntity<ApiResponseDTO<List<SyntheseDTO<String>>>> syntheseByZone() {
        List<SyntheseDTO<String>> data = colisService.getSyntheseByZone();
        return ResponseEntity.ok(ApiResponseDTO.success("Synthèse par zone", data));
    }

    @GetMapping("/synthese/statut")
    @Operation(summary = "Synthèse par statut")
    public ResponseEntity<ApiResponseDTO<List<SyntheseDTO<Colis.ColisStatus>>>> syntheseByStatut() {
        List<SyntheseDTO<Colis.ColisStatus>> data = colisService.getSyntheseByStatut();
        return ResponseEntity.ok(ApiResponseDTO.success("Synthèse par statut", data));
    }

    @GetMapping("/synthese/priorite")
    @Operation(summary = "Synthèse par priorité")
    public ResponseEntity<ApiResponseDTO<List<SyntheseDTO<Colis.Priorite>>>> syntheseByPriorite() {
        List<SyntheseDTO<Colis.Priorite>> data = colisService.getSyntheseByPriorite();
        return ResponseEntity.ok(ApiResponseDTO.success("Synthèse par priorité", data));
    }
}

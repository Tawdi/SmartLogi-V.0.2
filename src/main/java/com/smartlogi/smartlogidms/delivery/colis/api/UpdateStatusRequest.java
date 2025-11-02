package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotNull(message = "Status is required")
    private Colis.ColisStatus statut;
    private String commentaire;
    @NotNull(message = "utilisateur Id is required")
    private String utilisateurId;
}

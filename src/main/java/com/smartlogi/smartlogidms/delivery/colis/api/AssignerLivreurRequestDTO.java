package com.smartlogi.smartlogidms.delivery.colis.api;

import jakarta.validation.constraints.NotBlank;

public record AssignerLivreurRequestDTO(
        @NotBlank(message = "Driver Id is required (livreurId)")
        String livreurId
) {
}

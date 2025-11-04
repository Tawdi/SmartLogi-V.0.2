package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResponseDTO;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.driver.api.DriverResponseDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.shared.api.AdresseDTO;
import com.smartlogi.smartlogidms.masterdata.zone.api.ZoneResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColisResponseDTO extends BaseResponseDTO<String> {

    private String reference;
    private Double poids;
    private String description;
    private Colis.ColisStatus statut;
    private Colis.Priorite priorite;
    private ClientResponseDTO expediteur;
    private DriverResponseDTO livreur;
    private RecipientResponseDTO destinataire;
    private ZoneResponseDTO zone;
    private AdresseDTO adresseLivraison;
}

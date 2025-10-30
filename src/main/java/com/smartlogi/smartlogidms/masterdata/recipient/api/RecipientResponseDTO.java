package com.smartlogi.smartlogidms.masterdata.recipient.api;

import com.smartlogi.smartlogidms.masterdata.shared.api.PersonneResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecipientResponseDTO extends PersonneResponseDTO<String> {

    private String rue;
    private String ville;
    private String codePostal;

}

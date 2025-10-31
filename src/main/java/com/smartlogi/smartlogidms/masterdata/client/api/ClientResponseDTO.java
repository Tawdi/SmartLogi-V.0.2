package com.smartlogi.smartlogidms.masterdata.client.api;

import com.smartlogi.smartlogidms.masterdata.shared.api.PersonneResponseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ClientResponseDTO extends PersonneResponseDTO<String> {

    private String rue;
    private String ville;
    private String codePostal;


}

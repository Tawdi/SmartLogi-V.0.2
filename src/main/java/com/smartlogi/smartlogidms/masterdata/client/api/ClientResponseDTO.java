package com.smartlogi.smartlogidms.masterdata.client.api;

import com.smartlogi.smartlogidms.common.api.BaseResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClientResponseDTO extends BaseResponseDTO<UUID> {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String rue;
    private String ville;
    private String codePostal;


}

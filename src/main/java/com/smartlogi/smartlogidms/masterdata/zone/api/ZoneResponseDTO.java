package com.smartlogi.smartlogidms.masterdata.zone.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoneResponseDTO extends BaseResponseDTO<String> {

    private String name;
    private String codePostal;

}

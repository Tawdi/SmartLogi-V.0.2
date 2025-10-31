package com.smartlogi.smartlogidms.masterdata.zone.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResponseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ZoneResponseDTO extends BaseResponseDTO<String> {

    private String name;
    private String codePostal;

}

package com.smartlogi.smartlogidms.masterdata.driver.api;

import com.smartlogi.smartlogidms.common.api.BaseResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DriverResponseDTO extends BaseResponseDTO<String> {
    private String zoneAssigneeId;
    private String zoneAssigneeNom;

}

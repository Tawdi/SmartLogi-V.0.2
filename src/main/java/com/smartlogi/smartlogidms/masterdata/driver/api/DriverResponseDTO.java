package com.smartlogi.smartlogidms.masterdata.driver.api;


import com.smartlogi.smartlogidms.masterdata.shared.api.PersonneResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DriverResponseDTO extends PersonneResponseDTO<String> {
    private String zoneAssigneeId;
    private String zoneAssigneeNom;

}

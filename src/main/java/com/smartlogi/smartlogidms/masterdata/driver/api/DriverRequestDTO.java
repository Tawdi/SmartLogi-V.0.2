package com.smartlogi.smartlogidms.masterdata.driver.api;

import com.smartlogi.smartlogidms.masterdata.shared.api.PersonneRequestDTO;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DriverRequestDTO extends PersonneRequestDTO {

    @Size(max = 100)
    private String vehicule;
}

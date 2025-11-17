package com.smartlogi.smartlogidms.masterdata.zone.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResquestDTO;
import com.smartlogi.smartlogidms.common.api.dto.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(callSuper = true)
public class ZoneRequestDTO implements BaseResquestDTO {

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, message = "Le nom de zone est obligatoire")
    @Size(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, min = 3, max = 50, message = "Le nom de zone ne doit étre entre [3 - 50] caractères")
    private String name;

    @NotBlank(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, message = "Le code Postal de zone est obligatoire")
    @Size(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, min = 1, max = 20, message = "Le code Postal de zone ne doit entre [1 - 20] caractères")
    private String codePostal;
}

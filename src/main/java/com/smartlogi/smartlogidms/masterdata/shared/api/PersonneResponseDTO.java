package com.smartlogi.smartlogidms.masterdata.shared.api;

import com.smartlogi.smartlogidms.common.api.dto.BaseResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonneResponseDTO<ID> extends BaseResponseDTO<ID> {

    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;

}

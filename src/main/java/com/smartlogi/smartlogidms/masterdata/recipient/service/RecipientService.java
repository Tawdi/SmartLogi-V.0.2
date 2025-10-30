package com.smartlogi.smartlogidms.masterdata.recipient.service;

import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientResponseDTO;

import java.util.Optional;

public interface RecipientService extends StringCrudService<RecipientRequestDTO, RecipientResponseDTO> {

    RecipientResponseDTO findByEmail(String email);

}

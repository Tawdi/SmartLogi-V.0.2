package com.smartlogi.smartlogidms.masterdata.recipient.service;

import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;

import java.util.Optional;

public interface RecipientService extends StringCrudService<Recipient,RecipientRequestDTO, RecipientResponseDTO> {

    RecipientResponseDTO findByEmail(String email);

}

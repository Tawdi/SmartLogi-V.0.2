package com.smartlogi.smartlogidms.masterdata.recipient.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientMapper;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.RecipientRepository;
import org.springframework.stereotype.Service;

@Service
public class RecipientServiceImpl extends StringCrudServiceImpl<Recipient, RecipientRequestDTO, RecipientResponseDTO> implements RecipientService {

    private final RecipientRepository recipientRepository;
    private final RecipientMapper recipientMapper;

    public RecipientServiceImpl(RecipientRepository recipientRepository, RecipientMapper recipientMapper) {
        super(recipientRepository, recipientMapper);
        this.recipientRepository = recipientRepository;
        this.recipientMapper = recipientMapper;
    }

    @Override
    public RecipientResponseDTO findByEmail(String email) {
        Recipient entity = recipientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with email: " + email));
        return recipientMapper.toDto(entity);
    }
}

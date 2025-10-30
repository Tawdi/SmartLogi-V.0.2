package com.smartlogi.smartlogidms.masterdata.recipient.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientMapper;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientMapper;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.RecipientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecipientServiceImpl extends StringCrudServiceImpl<Recipient, RecipientRequestDTO, RecipientResponseDTO> implements RecipientService {

    private final RecipientRepository repository;
    private final RecipientMapper mapper;

    public RecipientServiceImpl(RecipientRepository recipientRepository, RecipientMapper recipientMapper) {
        super(recipientRepository, recipientMapper);
        this.repository = recipientRepository;
        this.mapper = recipientMapper;
    }

    //TODO
    @Override
    public RecipientResponseDTO findByEmail(String email) {
        Recipient entity = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with email: " + email));
        return mapper.toDto(entity);
    }
}

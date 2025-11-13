package com.smartlogi.smartlogidms.masterdata.client.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientMapper;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceImpl extends StringCrudServiceImpl<ClientExpediteur, ClientRequestDTO, ClientResponseDTO> implements ClientService {

    private final ClientExpediteurRepository clientExpediteurRepo;
    private final ClientMapper clientMapper;

    public ClientServiceImpl(ClientExpediteurRepository clientExpediteurRepository, ClientMapper clientMapper) {
        super(clientExpediteurRepository, clientMapper);
        this.clientExpediteurRepo = clientExpediteurRepository;
        this.clientMapper = clientMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO findByEmail(String email) {
        ClientExpediteur entity = clientExpediteurRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with email: " + email));
        return clientMapper.toDto(entity);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> searchClients(String keyword, Pageable pageable) {
        return clientExpediteurRepo.searchClients(keyword, pageable)
                .map(clientMapper::toDto);
    }
}

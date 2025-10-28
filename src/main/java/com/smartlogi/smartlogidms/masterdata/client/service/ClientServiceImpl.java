package com.smartlogi.smartlogidms.masterdata.client.service;

import com.smartlogi.smartlogidms.common.service.BaseCrudServiceImpl;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientMapper;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientServiceImpl extends BaseCrudServiceImpl<ClientExpediteur, ClientRequestDTO, ClientResponseDTO, UUID> implements ClientService {

    private final ClientExpediteurRepository repository;
    private final ClientMapper mapper;

    public ClientServiceImpl(ClientExpediteurRepository clientExpediteurRepository , ClientMapper clientMapper) {
        super(clientExpediteurRepository,clientMapper);
        this.repository = clientExpediteurRepository;
        this.mapper = clientMapper;
    }

    //TODO
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientResponseDTO> findByEmail(String email) {
        return Optional.empty();
    }

    //TODO
    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> searchClients(String keyword, Pageable pageable) {
        return null;
    }
}

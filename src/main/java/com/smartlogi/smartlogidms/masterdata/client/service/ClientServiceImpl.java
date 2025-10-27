package com.smartlogi.smartlogidms.masterdata.client.service;

import com.smartlogi.smartlogidms.common.service.BaseCrudServiceImpl;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClientServiceImpl extends BaseCrudServiceImpl<ClientExpediteur, UUID> implements ClientService {


    public ClientServiceImpl(ClientExpediteurRepository clientExpediteurRepository) {
        super(clientExpediteurRepository);
    }

    //TODO
    @Override
    @Transactional(readOnly = true)
    public Optional<ClientExpediteur> findByEmail(String email) {
        return Optional.empty();
    }

    //TODO
    @Override
    @Transactional(readOnly = true)
    public Page<ClientExpediteur> searchClients(String keyword, Pageable pageable) {
        return null;
    }
}

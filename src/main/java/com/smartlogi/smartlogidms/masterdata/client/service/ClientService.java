package com.smartlogi.smartlogidms.masterdata.client.service;

import com.smartlogi.smartlogidms.common.service.BaseCrudService;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ClientService extends BaseCrudService<ClientExpediteur, UUID> {

    Optional<ClientExpediteur> findByEmail(String email);

    Page<ClientExpediteur> searchClients(String keyword, Pageable pageable);

}

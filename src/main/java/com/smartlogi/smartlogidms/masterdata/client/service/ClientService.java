package com.smartlogi.smartlogidms.masterdata.client.service;

import com.smartlogi.smartlogidms.common.service.StringCrudService;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClientService extends StringCrudService<ClientRequestDTO, ClientResponseDTO> {

    ClientResponseDTO findByEmail(String email);

    Page<ClientResponseDTO> searchClients(String keyword, Pageable pageable);

}

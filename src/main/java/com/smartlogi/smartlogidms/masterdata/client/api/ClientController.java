package com.smartlogi.smartlogidms.masterdata.client.api;

import com.smartlogi.smartlogidms.common.api.controller.StringBaseController;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.service.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Client management APIs")
public class ClientController extends StringBaseController<ClientExpediteur, ClientRequestDTO, ClientResponseDTO> {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    public ClientController(ClientService clientService, ClientMapper clientMapper) {
        super(clientService, clientMapper);
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }


}

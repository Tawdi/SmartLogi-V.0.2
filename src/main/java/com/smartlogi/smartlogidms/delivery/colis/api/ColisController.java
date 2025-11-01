package com.smartlogi.smartlogidms.delivery.colis.api;

import com.smartlogi.smartlogidms.common.api.controller.StringBaseController;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.colis.service.ColisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/colis")
@Tag(name = "Colis", description = "Colis management APIs")
public class ColisController extends StringBaseController<Colis, ColisRequestDTO, ColisResponseDTO> {

    private final ColisService colisService;
    private final ColisMapper colisMapper;

    public ColisController(ColisService colisService, ColisMapper colisMapper) {
        super(colisService, colisMapper);
        this.colisService = colisService;
        this.colisMapper = colisMapper;
    }
}

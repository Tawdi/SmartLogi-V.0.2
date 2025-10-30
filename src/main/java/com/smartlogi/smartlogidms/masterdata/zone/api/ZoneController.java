package com.smartlogi.smartlogidms.masterdata.zone.api;

import com.smartlogi.smartlogidms.common.api.controller.StringBaseController;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.service.ZoneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zones")
@Tag(name = "Zones", description = "Zones management APIs")
public class ZoneController extends StringBaseController<Zone, ZoneRequestDTO, ZoneResponseDTO> {

    private final ZoneService zoneService;
    private final ZoneMapper zoneMapper;

    public ZoneController(ZoneService zoneService, ZoneMapper zoneMapper) {
        super(zoneService, zoneMapper);
        this.zoneService = zoneService;
        this.zoneMapper = zoneMapper;
    }
}

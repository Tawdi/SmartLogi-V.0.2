package com.smartlogi.smartlogidms.masterdata.driver.api;


import com.smartlogi.smartlogidms.common.api.controller.StringBaseController;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import com.smartlogi.smartlogidms.masterdata.driver.service.DriverService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Drivers", description = "Drivers management APIs")
public class DriverController extends StringBaseController<Driver, DriverRequestDTO, DriverResponseDTO> {

    private final DriverService driverService;
    private final DriverMapper driverMapper;

    protected DriverController(DriverService driverService, DriverMapper driverMapper) {
        super(driverService, driverMapper);
        this.driverService = driverService;
        this.driverMapper = driverMapper;
    }
}

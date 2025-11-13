package com.smartlogi.smartlogidms.masterdata.recipient.api;

import com.smartlogi.smartlogidms.common.api.controller.StringBaseController;
import com.smartlogi.smartlogidms.masterdata.recipient.service.RecipientService;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recipients")
@Tag(name = "Recipients", description = "Recipients management APIs")
public class RecipientController extends StringBaseController<Recipient, RecipientRequestDTO, RecipientResponseDTO> {


    public RecipientController(RecipientService recipientService, RecipientMapper recipientMapper) {
        super(recipientService, recipientMapper);

    }


}

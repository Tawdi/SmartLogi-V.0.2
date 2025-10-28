package com.smartlogi.smartlogidms.masterdata.client.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @GetMapping
    public ResponseEntity<String> getAll(){

        return ResponseEntity.ok("azerty azerty");
    }
}

package com.smartlogi.smartlogidms.delivery.colis.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.delivery.colis.api.ColisMapper;
import com.smartlogi.smartlogidms.delivery.colis.api.ColisRequestDTO;
import com.smartlogi.smartlogidms.delivery.colis.api.ColisResponseDTO;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.colis.domain.ColisRepository;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.RecipientRepository;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ColisServiceImpl extends StringCrudServiceImpl<Colis, ColisRequestDTO, ColisResponseDTO> implements ColisService {

    private final ColisRepository colisRepository;
    private final ColisMapper colisMapper;

    private final ClientExpediteurRepository expediteurRepo;
    private final RecipientRepository destinataireRepo;
    private final ZoneRepository zoneRepo;

    public ColisServiceImpl(ColisRepository colisRepository, ColisMapper colisMapper ,RecipientRepository destinataireRepo,ClientExpediteurRepository expediteurRepo,ZoneRepository zoneRepo) {
        super(colisRepository, colisMapper);
        this.colisRepository = colisRepository;
        this.colisMapper = colisMapper;
        this.expediteurRepo= expediteurRepo;
        this.destinataireRepo= destinataireRepo;
        this.zoneRepo= zoneRepo;

    }

    @Override
    @Transactional
    public ColisResponseDTO save(ColisRequestDTO requestDto) {
        Colis entity = mapper.toEntity(requestDto);
        entity.setExpediteur(loadExpediteur(requestDto.getExpediteurId()));
        entity.setDestinataire(loadDestinataire(requestDto.getDestinataireId()));
        entity.setZone(loadZone(requestDto.getZoneId()));
        Colis savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }


    @Override
    @Transactional
    public ColisResponseDTO update(String id, ColisRequestDTO requestDTO) {

        Colis existingEntity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        mapper.updateEntityFromDto(requestDTO, existingEntity);
        if (requestDTO.getExpediteurId() != null) {
            existingEntity.setExpediteur(loadExpediteur(requestDTO.getExpediteurId()));
        }
        if (requestDTO.getDestinataireId() != null) {
            existingEntity.setDestinataire(loadDestinataire(requestDTO.getDestinataireId()));
        }
        if (requestDTO.getZoneId() != null) {
            existingEntity.setZone(loadZone(requestDTO.getZoneId()));
        }
        Colis savedEntity = repository.save(existingEntity);
        return mapper.toDto(savedEntity);
    }

    private ClientExpediteur loadExpediteur(String id) {
        return expediteurRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expediteur not found: " + id));
    }

    private Recipient loadDestinataire(String id) {
        return destinataireRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found: " + id));
    }

    private Zone loadZone(String id) {
        return zoneRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found: " + id));
    }
}

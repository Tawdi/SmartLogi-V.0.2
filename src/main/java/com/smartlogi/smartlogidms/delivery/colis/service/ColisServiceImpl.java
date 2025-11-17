package com.smartlogi.smartlogidms.delivery.colis.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.common.service.EmailService;
import com.smartlogi.smartlogidms.common.service.implementation.StringCrudServiceImpl;
import com.smartlogi.smartlogidms.delivery.colis.api.*;
import com.smartlogi.smartlogidms.delivery.colis.domain.Colis;
import com.smartlogi.smartlogidms.delivery.colis.domain.ColisProduit;
import com.smartlogi.smartlogidms.delivery.colis.domain.ColisRepository;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonMapper;
import com.smartlogi.smartlogidms.delivery.historique.api.HistoriqueLivraisonResponseDTO;
import com.smartlogi.smartlogidms.delivery.historique.domain.HistoriqueLivraison;
import com.smartlogi.smartlogidms.delivery.historique.domain.HistoriqueLivraisonRepository;
import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import com.smartlogi.smartlogidms.delivery.product.domain.ProductRrepository;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import com.smartlogi.smartlogidms.masterdata.driver.domain.DriverRepository;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.RecipientRepository;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ColisServiceImpl extends StringCrudServiceImpl<Colis, ColisRequestDTO, ColisResponseDTO> implements ColisService {

    private final ColisRepository colisRepository;
    private final ColisMapper colisMapper;

    private final ClientExpediteurRepository expediteurRepo;
    private final RecipientRepository destinataireRepo;
    private final ZoneRepository zoneRepo;
    private final DriverRepository driverRepo;
    private final ProductRrepository productRrepo;

    private final EmailService emailService;

    private final HistoriqueLivraisonRepository historyRepo;
    private final HistoriqueLivraisonMapper historyMapper;

    @Value("${app.base-url}")
    private String baseUrl;

    public ColisServiceImpl(ColisRepository colisRepository, ColisMapper colisMapper,
                            RecipientRepository destinataireRepo,
                            ClientExpediteurRepository expediteurRepo, ZoneRepository zoneRepo,
                            DriverRepository driverRepo,
                            EmailService emailService,
                            ProductRrepository productRrepo,
                            HistoriqueLivraisonRepository historyRepo, HistoriqueLivraisonMapper historyMapper) {
        super(colisRepository, colisMapper);
        this.colisRepository = colisRepository;
        this.colisMapper = colisMapper;
        this.expediteurRepo = expediteurRepo;
        this.destinataireRepo = destinataireRepo;
        this.zoneRepo = zoneRepo;
        this.historyRepo = historyRepo;
        this.historyMapper = historyMapper;
        this.driverRepo = driverRepo;
        this.productRrepo = productRrepo;
        this.emailService = emailService;

    }

    @Override
    @Transactional
    public ColisResponseDTO save(ColisRequestDTO requestDto) {
        Colis entity = mapper.toEntity(requestDto);
        entity.setExpediteur(loadExpediteur(requestDto.getExpediteurId()));
        entity.setDestinataire(loadDestinataire(requestDto.getDestinataireId()));
        entity.setZone(loadZone(requestDto.getZoneId()));

        entity.getColisProduits().clear();
        Colis savedColis = repository.save(entity);

        if (requestDto.getProductList() != null && !requestDto.getProductList().isEmpty()) {
            savedColis.getColisProduits().clear();

            requestDto.getProductList().forEach(item -> {
                Product product = productRrepo.findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé: " + item.getProductId()));

                ColisProduit cp = new ColisProduit();
                cp.setColisId(savedColis.getId());
                cp.setProductId(product.getId());
                cp.setQuantite(item.getQuantite());
                cp.setPrixUnitaire(item.getPrix());
                cp.setColis(savedColis);
                cp.setProduct(product);

                savedColis.getColisProduits().add(cp);
            });
        }

        Colis finalSaved = repository.save(savedColis);
        sendCreationEmail(finalSaved);
        return colisMapper.toDto(finalSaved);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ColisResponseDTO> findByExpediteurId(String expediteurId, Colis.ColisStatus status, Pageable pageable) {
        Page<Colis> colisPage;

        if (status == null) {
            // All
            colisPage = colisRepository.findByExpediteurId(expediteurId, null, pageable);
        } else if (status == Colis.ColisStatus.DELIVERED) {
            // Delivered
            colisPage = colisRepository.findByExpediteurId(expediteurId, status, pageable);
        } else {
            // In progress (all except DELIVERED)
            List<Colis.ColisStatus> inProgressStatuses = List.of(
                    Colis.ColisStatus.CREATED,
                    Colis.ColisStatus.COLLECTED,
                    Colis.ColisStatus.IN_STOCK,
                    Colis.ColisStatus.IN_TRANSIT
            );
            colisPage = colisRepository.findByExpediteurIdAndStatuts(expediteurId, inProgressStatuses, pageable);
        }

        return colisPage.map(colisMapper::toDto);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ColisResponseDTO> findByDestinataireId(String destinataireId, Colis.ColisStatus status, Pageable pageable) {

        Page<Colis> colisPage = colisRepository.findByDestinataireId(destinataireId, status, pageable);

        return colisPage.map(colisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColisResponseDTO> findByLivreurId(String livreurId, Colis.ColisStatus status, Pageable pageable) {

        Page<Colis> colisPage = colisRepository.findByLivreurId(livreurId, status, pageable);

        return colisPage.map(colisMapper::toDto);
    }


    @Override
    @Transactional
    public ColisResponseDTO update(String id, ColisRequestDTO requestDTO) {

        Colis existingEntity = loadColis(id);

        colisMapper.updateEntityFromDto(requestDTO, existingEntity);
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
        return colisMapper.toDto(savedEntity);
    }


    @Override
    @Transactional
    public ColisResponseDTO updateStatus(String id, UpdateStatusRequest requestDTO) {
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colis not found: " + id));

        Colis.ColisStatus newStatus = requestDTO.getStatut();

        Colis.ColisStatus current = colis.getStatut();

        if (current == null) {
            throw new NullPointerException(
                    "Invalid status transition (cuerrent status is missing)"
            );
        }

        boolean isValidTransition = switch (current) {
            case CREATED -> newStatus == Colis.ColisStatus.COLLECTED;
            case COLLECTED -> newStatus == Colis.ColisStatus.IN_STOCK;
            case IN_STOCK -> newStatus == Colis.ColisStatus.IN_TRANSIT;
            case IN_TRANSIT -> newStatus == Colis.ColisStatus.DELIVERED;
            case DELIVERED -> false;
        };

        if (!isValidTransition) {
            throw new IllegalStateException(
                    "Invalid status transition: " + current + " → " + newStatus +
                            ". Allowed: " + getAllowedTransitions(current)
            );
        }

        colis.setStatut(newStatus);
        Colis saved = colisRepository.save(colis);

        // track
        HistoriqueLivraison history = new HistoriqueLivraison(saved, current, newStatus,
                requestDTO.getUtilisateurId(),
                requestDTO.getCommentaire().isEmpty() ? " Status Updated " : requestDTO.getCommentaire()); // will work with
        historyRepo.save(history);

        sendStatusEmail(saved, current, newStatus, requestDTO.getUtilisateurId());
        return colisMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistoriqueLivraisonResponseDTO> getHistory(String colisId, Pageable pageable) {
        return historyRepo.findByColisId(colisId, pageable)
                .map(historyMapper::toDto);
    }

    @Override
    @Transactional
    public ColisResponseDTO assignerLivreur(String colisId, AssignerLivreurRequestDTO request) {
        Colis colis = loadColis(colisId);
        Driver driver = loadDriver(request.livreurId());

        String oldDriverId = colis.getLivreur() != null ? colis.getLivreur().getId() : null;
        colis.setLivreur(driver);
        Colis saved = colisRepository.save(colis);

        String comment = oldDriverId == null
                ? "Assigned to driver ID=" + driver.getId()
                : "Reassigned from driver ID=" + oldDriverId + " to driver ID=" + driver.getId();
        HistoriqueLivraison history = new HistoriqueLivraison(
                saved,
                colis.getStatut(),
                colis.getStatut(),
                "MANAGER",
                comment
        );
        historyRepo.save(history);
        return colisMapper.toDto(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<SyntheseDTO<String>> getSyntheseByZone() {
        return colisRepository.countByZone();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyntheseDTO<Colis.ColisStatus>> getSyntheseByStatut() {
        return colisRepository.countByStatut();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SyntheseDTO<Colis.Priorite>> getSyntheseByPriorite() {
        return colisRepository.countByPriorite();
    }

    private Colis loadColis(String id) {
        return colisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colis not found: " + id));
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

    private Driver loadDriver(String id) {
        return driverRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + id));
    }

    private String getAllowedTransitions(Colis.ColisStatus current) {
        return switch (current) {
            case CREATED -> "COLLECTED";
            case COLLECTED -> "IN_STOCK";
            case IN_STOCK -> "IN_TRANSIT";
            case IN_TRANSIT -> "DELIVERED";
            case DELIVERED -> "none (final state)";
        };
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ColisProductResponseDTO> getProductsByColisId(String colisId, Pageable pageable) {
        Page<ColisProduit> page = colisRepository.findColisProduitsByColisId(colisId, pageable);
        return page.map(cp -> {
            ColisProductResponseDTO dto = new ColisProductResponseDTO();
            dto.setProductId(cp.getProduct().getId());
            dto.setNom(cp.getProduct().getNom());
            dto.setCategorie(cp.getProduct().getCategorie());
            dto.setPoids(cp.getProduct().getPoids());
            dto.setQuantite(cp.getQuantite());
            dto.setPrixUnitaire(cp.getPrixUnitaire());
            dto.setPrixTotal(cp.getQuantite() * cp.getPrixUnitaire());
            return dto;
        });
    }


    private void sendCreationEmail(Colis colis) {
        String toSender = colis.getExpediteur().getEmail();
        String toRecipient = colis.getDestinataire().getEmail();
        String subject = "Nouveau colis créé: " + colis.getReference();
        String message = """
                Bonjour,
                
                Votre colis %s a été créé avec succès.
                Statut actuel: CREATED
                Poids: %.1f kg
                Priorité: %s
                
                Suivi: %s/api/colis/%s
                
                Cordialement,
                SmartLogi
                """.formatted(colis.getReference(), colis.getPoids(), colis.getPriorite(), baseUrl, colis.getId());

        emailService.sendNotification(toSender, subject, message);
        emailService.sendNotification(toRecipient, subject, message);
    }

    private void sendStatusEmail(Colis colis, Colis.ColisStatus oldStatus, Colis.ColisStatus newStatus, String userId) {
        String toSender = colis.getExpediteur().getEmail();
        String subject = "Mise à jour statut: " + colis.getReference();
        String message = """
                Bonjour,
                
                Votre colis %s a changé de statut:
                • Ancien: %s
                • Nouveau: %s
                • Par: %s
                • Date: %s
                
                Suivi: %s/api/colis/%s
                
                Cordialement,
                SmartLogi
                """.formatted(colis.getReference(), oldStatus, newStatus, userId, LocalDateTime.now(), baseUrl, colis.getId());

        emailService.sendNotification(toSender, subject, message);
    }
}

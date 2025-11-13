package com.smartlogi.smartlogidms.delivery.colis.domain;

import com.smartlogi.smartlogidms.delivery.colis.api.SyntheseDTO;
import com.smartlogi.smartlogidms.delivery.product.domain.Product;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.driver.domain.Driver;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.zone.domain.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.check_nullability=true"
})
class ColisRepositoryTest {

    @Autowired
    private ColisRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository clientRepository;

    @Autowired
    private com.smartlogi.smartlogidms.masterdata.recipient.domain.RecipientRepository recipientRepository;

    @Autowired
    private com.smartlogi.smartlogidms.masterdata.driver.domain.DriverRepository driverRepository;

    @Autowired
    private com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository zoneRepository;

    @Autowired
    private com.smartlogi.smartlogidms.delivery.product.domain.ProductRrepository productRepository;

    private ClientExpediteur expediteur1;
    private ClientExpediteur expediteur2;
    private Recipient destinataire1;
    private Recipient destinataire2;
    private Driver livreur1;
    private Driver livreur2;
    private Zone zone1;
    private Zone zone2;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.clear();

        // Create test zones
        zone1 = createZone("Zone Paris Center", "75001");
        zone2 = createZone("Zone Lyon Center", "69001");
        zoneRepository.save(zone1);
        zoneRepository.save(zone2);

        // Create test clients (expediteurs)
        expediteur1 = createClientExpediteur("Client1", "Company1", "client1@example.com", "+33123456789");
        expediteur2 = createClientExpediteur("Client2", "Company2", "client2@example.com", "+33987654321");
        clientRepository.save(expediteur1);
        clientRepository.save(expediteur2);

        // Create test recipients
        destinataire1 = createRecipient("Recipient1", "Last1", "recipient1@example.com", "+33111111111");
        destinataire2 = createRecipient("Recipient2", "Last2", "recipient2@example.com", "+33222222222");
        recipientRepository.save(destinataire1);
        recipientRepository.save(destinataire2);

        // Create test drivers
        livreur1 = createDriver("Driver1", "Last1", "driver1@example.com", "+33333333333", "Van 1", zone1);
        livreur2 = createDriver("Driver2", "Last2", "driver2@example.com", "+33444444444", "Van 2", zone2);
        driverRepository.save(livreur1);
        driverRepository.save(livreur2);

        // Create test products
        product1 = createProduct("Product1", "Electronics", 1.5, 99.99);
        product2 = createProduct("Product2", "Clothing", 0.5, 49.99);
        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    void shouldSaveColisSuccessfully() {
        // Given
        Colis newColis = createColis(
                "REF001", 5.0, "Test package",
                Colis.ColisStatus.CREATED, Colis.Priorite.MEDIUM,
                expediteur1, destinataire1, null, zone1,
                new Adresse("Paris", "123 Main St", "75001")
        );

        // When
        Colis saved = repository.save(newColis);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getReference()).isEqualTo("REF001");
        assertThat(saved.getPoids()).isEqualTo(5.0);
        assertThat(saved.getDescription()).isEqualTo("Test package");
        assertThat(saved.getStatut()).isEqualTo(Colis.ColisStatus.CREATED);
        assertThat(saved.getPriorite()).isEqualTo(Colis.Priorite.MEDIUM);
        assertThat(saved.getExpediteur().getId()).isEqualTo(expediteur1.getId());
        assertThat(saved.getDestinataire().getId()).isEqualTo(destinataire1.getId());
        assertThat(saved.getZone().getId()).isEqualTo(zone1.getId());
        assertThat(saved.getAdresseLivraison().getVille()).isEqualTo("Paris");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindColisById() {
        // Given
        Colis savedColis = repository.save(createColis(
                "REF001", 5.0, "Test package",
                Colis.ColisStatus.CREATED, Colis.Priorite.MEDIUM,
                expediteur1, destinataire1, livreur1, zone1,
                new Adresse("Paris", "123 Main St", "75001")
        ));

        // When
        Optional<Colis> found = repository.findById(savedColis.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getReference()).isEqualTo("REF001");
        assertThat(found.get().getPoids()).isEqualTo(5.0);
        assertThat(found.get().getStatut()).isEqualTo(Colis.ColisStatus.CREATED);
        assertThat(found.get().getExpediteur().getFirstName()).isEqualTo("Client1");
        assertThat(found.get().getDestinataire().getFirstName()).isEqualTo("Recipient1");
        assertThat(found.get().getLivreur().getFirstName()).isEqualTo("Driver1");
    }

    @Test
    void shouldReturnEmptyWhenColisNotFoundById() {
        // When
        Optional<Colis> found = repository.findById("non-existent-id");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllColisWithPagination() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire2, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.DELIVERED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone1));
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Colis> colisPage = repository.findAll(pageable);

        // Then
        assertThat(colisPage.getContent()).hasSize(2);
        assertThat(colisPage.getTotalElements()).isEqualTo(3);
        assertThat(colisPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldFindColisByStatut() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire2, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.CREATED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone1));
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Colis> result = repository.findByStatut(Colis.ColisStatus.CREATED, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Colis::getReference)
                .containsExactlyInAnyOrder("REF001", "REF003");
    }

    @Test
    void shouldFindColisByExpediteurId() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire2, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.DELIVERED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone1));
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Colis> result = repository.findByExpediteurId(expediteur1.getId(), null, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Colis::getReference)
                .containsExactlyInAnyOrder("REF001", "REF003");
    }

    @Test
    void shouldFindColisByExpediteurIdAndStatut() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur1, destinataire2, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.DELIVERED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone1));
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Colis> result = repository.findByExpediteurId(expediteur1.getId(), Colis.ColisStatus.CREATED, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getReference()).isEqualTo("REF001");
        assertThat(result.getContent().get(0).getStatut()).isEqualTo(Colis.ColisStatus.CREATED);
    }

    @Test
    void shouldFindColisByExpediteurIdAndStatuts() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur1, destinataire2, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.DELIVERED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone1));
        Pageable pageable = PageRequest.of(0, 10);
        List<Colis.ColisStatus> statuts = List.of(Colis.ColisStatus.CREATED, Colis.ColisStatus.IN_TRANSIT);

        // When
        Page<Colis> result = repository.findByExpediteurIdAndStatuts(expediteur1.getId(), statuts, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Colis::getReference)
                .containsExactlyInAnyOrder("REF001", "REF002");
    }

    @Test
    void shouldFindColisByDestinataireId() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire1, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.DELIVERED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone1));
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Colis> result = repository.findByDestinataireId(destinataire1.getId(), null, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Colis::getReference)
                .containsExactlyInAnyOrder("REF001", "REF002");
    }

    @Test
    void shouldFindColisByDestinataireIdAndStatut() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire1, livreur1, zone2));
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Colis> result = repository.findByDestinataireId(destinataire1.getId(), Colis.ColisStatus.CREATED, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getReference()).isEqualTo("REF001");
        assertThat(result.getContent().get(0).getStatut()).isEqualTo(Colis.ColisStatus.CREATED);
    }

    @Test
    void shouldFindColisByLivreurId() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, livreur1, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire2, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.DELIVERED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone1));
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Colis> result = repository.findByLivreurId(livreur1.getId(), null, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Colis::getReference)
                .containsExactlyInAnyOrder("REF001", "REF002");
    }

    @Test
    void shouldFindColisByLivreurIdAndStatut() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.LOW, expediteur1, destinataire1, livreur1, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.DELIVERED, Colis.Priorite.MEDIUM, expediteur2, destinataire2, livreur1, zone2));
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Colis> result = repository.findByLivreurId(livreur1.getId(), Colis.ColisStatus.IN_TRANSIT, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getReference()).isEqualTo("REF001");
        assertThat(result.getContent().get(0).getStatut()).isEqualTo(Colis.ColisStatus.IN_TRANSIT);
    }

    @Test
    void shouldCountByZone() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire2, livreur1, zone1));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.DELIVERED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone2));

        // When
        List<SyntheseDTO<String>> result = repository.countByZone();

        // Then
        assertThat(result).hasSize(2);

        // Find zone1 stats
        SyntheseDTO<String> zone1Stats = result.stream()
                .filter(dto -> dto.key().equals("Zone Paris Center"))
                .findFirst()
                .orElse(null);
        assertThat(zone1Stats).isNotNull();
        assertThat(zone1Stats.count()).isEqualTo(2L);
        assertThat(zone1Stats.poidsTotal()).isEqualTo(3.0); // 1.0 + 2.0

        // Find zone2 stats
        SyntheseDTO<String> zone2Stats = result.stream()
                .filter(dto -> dto.key().equals("Zone Lyon Center"))
                .findFirst()
                .orElse(null);
        assertThat(zone2Stats).isNotNull();
        assertThat(zone2Stats.count()).isEqualTo(1L);
        assertThat(zone2Stats.poidsTotal()).isEqualTo(3.0);
    }

    @Test
    void shouldCountByStatut() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire2, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.CREATED, Colis.Priorite.HIGH, expediteur1, destinataire2, livreur2, zone1));

        // When
        List<SyntheseDTO<Colis.ColisStatus>> result = repository.countByStatut();

        // Then
        assertThat(result).hasSize(2);

        // Check CREATED status
        SyntheseDTO<Colis.ColisStatus> createdStats = result.stream()
                .filter(dto -> dto.key() == Colis.ColisStatus.CREATED)
                .findFirst()
                .orElse(null);
        assertThat(createdStats).isNotNull();
        assertThat(createdStats.count()).isEqualTo(2L);
        assertThat(createdStats.poidsTotal()).isEqualTo(4.0); // 1.0 + 3.0

        // Check IN_TRANSIT status
        SyntheseDTO<Colis.ColisStatus> transitStats = result.stream()
                .filter(dto -> dto.key() == Colis.ColisStatus.IN_TRANSIT)
                .findFirst()
                .orElse(null);
        assertThat(transitStats).isNotNull();
        assertThat(transitStats.count()).isEqualTo(1L);
        assertThat(transitStats.poidsTotal()).isEqualTo(2.0);
    }

    @Test
    void shouldCountByPriorite() {
        // Given
        repository.save(createColis("REF001", 1.0, "Desc1", Colis.ColisStatus.CREATED, Colis.Priorite.LOW, expediteur1, destinataire1, null, zone1));
        repository.save(createColis("REF002", 2.0, "Desc2", Colis.ColisStatus.IN_TRANSIT, Colis.Priorite.MEDIUM, expediteur2, destinataire2, livreur1, zone2));
        repository.save(createColis("REF003", 3.0, "Desc3", Colis.ColisStatus.DELIVERED, Colis.Priorite.MEDIUM, expediteur1, destinataire2, livreur2, zone1));

        // When
        List<SyntheseDTO<Colis.Priorite>> result = repository.countByPriorite();

        // Then
        assertThat(result).hasSize(2);

        // Check MEDIUM priority
        SyntheseDTO<Colis.Priorite> mediumStats = result.stream()
                .filter(dto -> dto.key() == Colis.Priorite.MEDIUM)
                .findFirst()
                .orElse(null);
        assertThat(mediumStats).isNotNull();
        assertThat(mediumStats.count()).isEqualTo(2L);
        assertThat(mediumStats.poidsTotal()).isEqualTo(5.0); // 2.0 + 3.0

        // Check LOW priority
        SyntheseDTO<Colis.Priorite> lowStats = result.stream()
                .filter(dto -> dto.key() == Colis.Priorite.LOW)
                .findFirst()
                .orElse(null);
        assertThat(lowStats).isNotNull();
        assertThat(lowStats.count()).isEqualTo(1L);
        assertThat(lowStats.poidsTotal()).isEqualTo(1.0);
    }

    @Test
    void shouldFindColisProduitsByColisId() {
        // Given
        Colis colis = createColis("REF001", 5.0, "Test package",
                Colis.ColisStatus.CREATED, Colis.Priorite.MEDIUM,
                expediteur1, destinataire1, livreur1, zone1,
                new Adresse("Paris", "123 Main St", "75001")
        );
        Colis savedColis = repository.save(colis);

        // Create ColisProduit entries
        ColisProduit cp1 = createColisProduit(savedColis.getId(), product1.getId(), 2, 99.99);
        ColisProduit cp2 = createColisProduit(savedColis.getId(), product2.getId(), 1, 49.99);
        entityManager.persist(cp1);
        entityManager.persist(cp2);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ColisProduit> result = repository.findColisProduitsByColisId(savedColis.getId(), pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(ColisProduit::getQuantite)
                .containsExactlyInAnyOrder(2, 1);
    }

    @Test
    void shouldUpdateColisStatut() {
        // Given
        Colis savedColis = repository.save(createColis(
                "REF001", 5.0, "Test package",
                Colis.ColisStatus.CREATED, Colis.Priorite.MEDIUM,
                expediteur1, destinataire1, livreur1, zone1,
                new Adresse("Paris", "123 Main St", "75001")
        ));

        // When
        savedColis.setStatut(Colis.ColisStatus.IN_TRANSIT);
        Colis updated = repository.save(savedColis);

        // Then
        assertThat(updated.getStatut()).isEqualTo(Colis.ColisStatus.IN_TRANSIT);
        assertThat(updated.getReference()).isEqualTo("REF001"); // Other fields unchanged
    }

    @Test
    void shouldUpdateColisLivreur() {
        // Given
        Colis savedColis = repository.save(createColis(
                "REF001", 5.0, "Test package",
                Colis.ColisStatus.CREATED, Colis.Priorite.MEDIUM,
                expediteur1, destinataire1, null, zone1,
                new Adresse("Paris", "123 Main St", "75001")
        ));

        // When
        savedColis.setLivreur(livreur1);
        Colis updated = repository.save(savedColis);

        // Then
        assertThat(updated.getLivreur()).isNotNull();
        assertThat(updated.getLivreur().getId()).isEqualTo(livreur1.getId());
    }

    @Test
    void shouldDeleteColisSuccessfully() {
        // Given
        Colis savedColis = repository.save(createColis(
                "REF001", 5.0, "Test package",
                Colis.ColisStatus.CREATED, Colis.Priorite.MEDIUM,
                expediteur1, destinataire1, livreur1, zone1,
                new Adresse("Paris", "123 Main St", "75001")
        ));
        String colisId = savedColis.getId();

        // When
        repository.deleteById(colisId);
        Optional<Colis> found = repository.findById(colisId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldSaveColisWithProducts() {
        // Given
        Colis colis = createColis(
                "REF001", 5.0, "Test package with products",
                Colis.ColisStatus.CREATED, Colis.Priorite.MEDIUM,
                expediteur1, destinataire1, livreur1, zone1,
                new Adresse("Paris", "123 Main St", "75001")
        );

        // Add products to colis
        colis.addProduit(product1, 2, product1.getPrix());
        colis.addProduit(product2, 1, product2.getPrix());

        // When
        Colis saved = repository.save(colis);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getColisProduits()).hasSize(2);
    }

    @Test
    void shouldHandleColisWithMaximumFieldLengths() {
        // Given
        String maxLengthReference = "R".repeat(50);
        String maxLengthDescription = "D".repeat(255);

        Colis colis = createColis(
                maxLengthReference, 5.0, maxLengthDescription,
                Colis.ColisStatus.CREATED, Colis.Priorite.MEDIUM,
                expediteur1, destinataire1, livreur1, zone1,
                new Adresse("Paris", "123 Main St", "75001")
        );

        // When
        Colis saved = repository.save(colis);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getReference()).hasSize(50);
        assertThat(saved.getDescription()).hasSize(255);
    }

    // Helper methods
    private Colis createColis(String reference, Double poids, String description,
                              Colis.ColisStatus statut, Colis.Priorite priorite,
                              ClientExpediteur expediteur, Recipient destinataire,
                              Driver livreur, Zone zone) {
        return createColis(reference, poids, description, statut, priorite,
                expediteur, destinataire, livreur, zone,
                new Adresse("Default City", "Default Street", "00000"));
    }

    private Colis createColis(String reference, Double poids, String description,
                              Colis.ColisStatus statut, Colis.Priorite priorite,
                              ClientExpediteur expediteur, Recipient destinataire,
                              Driver livreur, Zone zone, Adresse adresseLivraison) {
        Colis colis = new Colis();
        colis.setReference(reference);
        colis.setPoids(poids);
        colis.setDescription(description);
        colis.setStatut(statut);
        colis.setPriorite(priorite);
        colis.setExpediteur(expediteur);
        colis.setDestinataire(destinataire);
        colis.setLivreur(livreur);
        colis.setZone(zone);
        colis.setAdresseLivraison(adresseLivraison);
        return colis;
    }

    private ClientExpediteur createClientExpediteur(String firstName, String lastName, String email, String phoneNumber) {
        return new ClientExpediteur(firstName, lastName, email, phoneNumber,
                new Adresse("Client City", "Client Street", "00000"));
    }

    private Recipient createRecipient(String firstName, String lastName, String email, String phoneNumber) {
        return new Recipient(firstName, lastName, email, phoneNumber,
                new Adresse("Recipient City", "Recipient Street", "00000"));
    }

    private Driver createDriver(String firstName, String lastName, String email, String phoneNumber, String vehicule, Zone zone) {
        Driver driver = new Driver(firstName, lastName, email, phoneNumber, vehicule);
        driver.setZoneAssignee(zone);
        return driver;
    }

    private Zone createZone(String name, String codePostal) {
        Zone zone = new Zone();
        zone.setName(name);
        zone.setCodePostal(codePostal);
        return zone;
    }

    private Product createProduct(String nom, String categorie, Double poids, Double prix) {
        Product product = new Product();
        product.setNom(nom);
        product.setCategorie(categorie);
        product.setPoids(poids);
        product.setPrix(prix);
        return product;
    }

    private ColisProduit createColisProduit(String colisId, String productId, Integer quantite, Double prixUnitaire) {
        ColisProduit cp = new ColisProduit();
        cp.setColisId(colisId);
        cp.setProductId(productId);
        cp.setQuantite(quantite);
        cp.setPrixUnitaire(prixUnitaire);
        cp.setDateAjout(LocalDateTime.now());
        return cp;
    }
}
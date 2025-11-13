package com.smartlogi.smartlogidms.masterdata.driver.domain;

import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne.PersonneRole;
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
class DriverRepositoryTest {

    @Autowired
    private DriverRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private com.smartlogi.smartlogidms.masterdata.zone.domain.ZoneRepository zoneRepository;

    private Driver driver1;
    private Driver driver2;
    private Driver driver3;
    private Zone zone1;
    private Zone zone2;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.clear();

        // Create test zones
        zone1 = createZone("Zone Paris Center", "75001");
        zone2 = createZone("Zone Lyon Center", "69001");
        zoneRepository.save(zone1);
        zoneRepository.save(zone2);

        // Create test drivers
        driver1 = createDriver(
                "Jean", "Dupont", "jean.dupont@example.com", "+33123456789",
                "Renault Kangoo", zone1
        );

        driver2 = createDriver(
                "Marie", "Martin", "marie.martin@example.com", "+33987654321",
                "Peugeot Partner", zone1
        );

        driver3 = createDriver(
                "Pierre", "Bernard", "pierre.bernard@example.com", "+33556677889",
                "Citroën Berlingo", zone2
        );
    }

    @Test
    void shouldSaveDriverSuccessfully() {
        // Given
        Driver newDriver = createDriver(
                "Luc", "Moreau", "luc.moreau@example.com", "+33445566778",
                "Ford Transit", zone2
        );

        // When
        Driver saved = repository.save(newDriver);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getFirstName()).isEqualTo("Luc");
        assertThat(saved.getLastName()).isEqualTo("Moreau");
        assertThat(saved.getEmail()).isEqualTo("luc.moreau@example.com");
        assertThat(saved.getPhoneNumber()).isEqualTo("+33445566778");
        assertThat(saved.getVehicule()).isEqualTo("Ford Transit");
        assertThat(saved.getRole()).isEqualTo(PersonneRole.DRIVER);
        assertThat(saved.getZoneAssignee()).isNotNull();
        assertThat(saved.getZoneAssignee().getId()).isEqualTo(zone2.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindDriverById() {
        // Given
        Driver savedDriver = repository.save(driver1);

        // When
        Optional<Driver> found = repository.findById(savedDriver.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jean");
        assertThat(found.get().getLastName()).isEqualTo("Dupont");
        assertThat(found.get().getEmail()).isEqualTo("jean.dupont@example.com");
        assertThat(found.get().getVehicule()).isEqualTo("Renault Kangoo");
        assertThat(found.get().getZoneAssignee().getName()).isEqualTo("Zone Paris Center");
    }

    @Test
    void shouldReturnEmptyWhenDriverNotFoundById() {
        // When
        Optional<Driver> found = repository.findById("non-existent-id");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllDrivers() {
        // Given
        repository.save(driver1);
        repository.save(driver2);
        repository.save(driver3);

        // When
        List<Driver> drivers = repository.findAll();

        // Then
        assertThat(drivers).hasSize(3);
        assertThat(drivers)
                .extracting(Driver::getFirstName)
                .containsExactlyInAnyOrder("Jean", "Marie", "Pierre");
    }

    @Test
    void shouldFindAllDriversWithPagination() {
        // Given
        repository.save(driver1);
        repository.save(driver2);
        repository.save(driver3);
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Driver> driverPage = repository.findAll(pageable);

        // Then
        assertThat(driverPage.getContent()).hasSize(2);
        assertThat(driverPage.getTotalElements()).isEqualTo(3);
        assertThat(driverPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldUpdateDriverSuccessfully() {
        // Given
        Driver savedDriver = repository.save(driver1);
        String originalId = savedDriver.getId();

        // When - Update the driver
        savedDriver.setFirstName("Jean-Paul");
        savedDriver.setLastName("Dupont-Updated");
        savedDriver.setEmail("jeanpaul.dupont@example.com");
        savedDriver.setPhoneNumber("+33998877665");
        savedDriver.setVehicule("Mercedes Sprinter");
        savedDriver.setZoneAssignee(zone2);
        Driver updated = repository.save(savedDriver);

        // Then
        assertThat(updated.getId()).isEqualTo(originalId);
        assertThat(updated.getFirstName()).isEqualTo("Jean-Paul");
        assertThat(updated.getLastName()).isEqualTo("Dupont-Updated");
        assertThat(updated.getEmail()).isEqualTo("jeanpaul.dupont@example.com");
        assertThat(updated.getPhoneNumber()).isEqualTo("+33998877665");
        assertThat(updated.getVehicule()).isEqualTo("Mercedes Sprinter");
        assertThat(updated.getZoneAssignee().getId()).isEqualTo(zone2.getId());
    }

    @Test
    void shouldDeleteDriverSuccessfully() {
        // Given
        Driver savedDriver = repository.save(driver1);
        String driverId = savedDriver.getId();

        // When
        repository.deleteById(driverId);
        Optional<Driver> found = repository.findById(driverId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfDriverExistsById() {
        // Given
        Driver savedDriver = repository.save(driver1);

        // When
        boolean exists = repository.existsById(savedDriver.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfDriverDoesNotExistById() {
        // When
        boolean exists = repository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldCountDriversCorrectly() {
        // Given
        repository.save(driver1);
        repository.save(driver2);

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldDeleteAllDrivers() {
        // Given
        repository.save(driver1);
        repository.save(driver2);

        // When
        repository.deleteAll();
        List<Driver> allDrivers = repository.findAll();

        // Then
        assertThat(allDrivers).isEmpty();
    }

    @Test
    void shouldFindDriverByEmail() {
        // Given
        repository.save(driver1);
        repository.save(driver2);

        // When
        Optional<Driver> found = repository.findByEmail("jean.dupont@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jean");
        assertThat(found.get().getLastName()).isEqualTo("Dupont");
        assertThat(found.get().getVehicule()).isEqualTo("Renault Kangoo");
    }

    @Test
    void shouldReturnEmptyWhenDriverNotFoundByEmail() {
        // When
        Optional<Driver> found = repository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindDriversByZoneAssigneeId() {
        // Given
        repository.save(driver1);
        repository.save(driver2);
        repository.save(driver3);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Driver> result = repository.findByZoneAssigneeId(zone1.getId(), pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Driver::getFirstName)
                .containsExactlyInAnyOrder("Jean", "Marie");
        assertThat(result.getContent())
                .extracting(driver -> driver.getZoneAssignee().getId())
                .containsOnly(zone1.getId());
    }

    @Test
    void shouldReturnEmptyPageWhenNoDriversInZone() {
        // Given
        Zone emptyZone = createZone("Empty Zone", "00000");
        zoneRepository.save(emptyZone);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Driver> result = repository.findByZoneAssigneeId(emptyZone.getId(), pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void shouldFindDriversByZoneWithPagination() {
        // Given - Add more drivers to zone1
        Driver driver4 = createDriver("Driver4", "Last4", "driver4@test.com", "+1111111111", "Van 1", zone1);
        Driver driver5 = createDriver("Driver5", "Last5", "driver5@test.com", "+2222222222", "Van 2", zone1);
        Driver driver6 = createDriver("Driver6", "Last6", "driver6@test.com", "+3333333333", "Van 3", zone1);

        repository.save(driver1); // zone1
        repository.save(driver4); // zone1
        repository.save(driver5); // zone1
        repository.save(driver6); // zone1
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Driver> result = repository.findByZoneAssigneeId(zone1.getId(), pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldSearchDriversByLastName() {
        // Given
        repository.save(driver1); // Jean Dupont
        repository.save(driver2); // Marie Martin
        repository.save(driver3); // Pierre Bernard
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Driver> result = repository.searchDrivers("Dupont", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Dupont");
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Jean");
    }

    @Test
    void shouldSearchDriversByFirstName() {
        // Given
        repository.save(driver1); // Jean Dupont
        repository.save(driver2); // Marie Martin
        repository.save(driver3); // Pierre Bernard
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Driver> result = repository.searchDrivers("Marie", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Marie");
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Martin");
    }

    @Test
    void shouldSearchDriversByPhoneNumber() {
        // Given
        repository.save(driver1); // +33123456789
        repository.save(driver2); // +33987654321
        repository.save(driver3); // +33556677889
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Driver> result = repository.searchDrivers("123456789", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPhoneNumber()).isEqualTo("+33123456789");
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Jean");
    }

    @Test
    void shouldSearchDriversCaseInsensitive() {
        // Given
        repository.save(driver1); // Jean Dupont
        repository.save(driver2); // Marie Martin
        Pageable pageable = PageRequest.of(0, 10);

        // When - search with different case
        Page<Driver> result = repository.searchDrivers("JEAN", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Jean");
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Dupont");
    }

    @Test
    void shouldSearchDriversWithPartialMatch() {
        // Given
        repository.save(driver1); // Jean Dupont
        repository.save(driver2); // Marie Martin
        repository.save(driver3); // Pierre Bernard
        Pageable pageable = PageRequest.of(0, 10);

        // When - partial search
        Page<Driver> result = repository.searchDrivers("Mar", pageable);

        // Then - should match both Marie and Martin
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Marie");
    }

    @Test
    void shouldReturnEmptyPageWhenNoSearchResults() {
        // Given
        repository.save(driver1);
        repository.save(driver2);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Driver> result = repository.searchDrivers("NonexistentName", pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void shouldSearchDriversWithPagination() {
        // Given
        repository.save(createDriver("John", "Doe", "john1@test.com", "+1111111111", "Van 1", zone1));
        repository.save(createDriver("John", "Smith", "john2@test.com", "+2222222222", "Van 2", zone1));
        repository.save(createDriver("John", "Brown", "john3@test.com", "+3333333333", "Van 3", zone1));
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Driver> result = repository.searchDrivers("John", pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldSaveDriverWithSpecialCharacters() {
        // Given
        Driver driverWithSpecialChars = createDriver(
                "José", "Muñoz", "josé.muñoz@example.com", "+33123456789",
                "Renault Kangoo", zone1
        );

        // When
        Driver saved = repository.save(driverWithSpecialChars);
        Optional<Driver> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("José");
        assertThat(found.get().getLastName()).isEqualTo("Muñoz");
        assertThat(found.get().getEmail()).isEqualTo("josé.muñoz@example.com");
    }

    @Test
    void shouldHandleDriverWithMaximumAllowedFieldLengths() {
        // Given - Fields at maximum length
        String maxLengthFirstName = "A".repeat(50);
        String maxLengthLastName = "B".repeat(50);
        String maxLengthEmail = "C".repeat(100);
        String maxLengthPhone = "1".repeat(20);
        String maxLengthVehicule = "V".repeat(100);

        Driver driverWithMaxFields = createDriver(
                maxLengthFirstName, maxLengthLastName, maxLengthEmail, maxLengthPhone,
                maxLengthVehicule, zone1
        );

        // When
        Driver saved = repository.save(driverWithMaxFields);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getFirstName()).hasSize(50);
        assertThat(saved.getLastName()).hasSize(50);
        assertThat(saved.getEmail()).hasSize(100);
        assertThat(saved.getPhoneNumber()).hasSize(20);
        assertThat(saved.getVehicule()).hasSize(100);
    }

    @Test
    void shouldSaveDriverWithNullEmail() {
        // Given - Email can be null according to entity definition
        Driver driverWithNullEmail = createDriver(
                "No", "Email", null, "+1234567890",
                "Test Vehicle", zone1
        );

        // When
        Driver saved = repository.save(driverWithNullEmail);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isNull();
        assertThat(saved.getFirstName()).isEqualTo("No");
    }

    @Test
    void shouldSaveDriverWithoutZoneAssignee() {
        // Given
        Driver driverWithoutZone = createDriver(
                "No", "Zone", "no.zone@example.com", "+1234567890",
                "Test Vehicle", null
        );

        // When
        Driver saved = repository.save(driverWithoutZone);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getZoneAssignee()).isNull();
        assertThat(saved.getFirstName()).isEqualTo("No");
    }

    @Test
    void shouldMaintainDriverRoleAutomatically() {
        // Given
        Driver newDriver = createDriver(
                "Test", "Driver", "test@example.com", "+1234567890",
                "Test Vehicle", zone1
        );

        // When
        Driver saved = repository.save(newDriver);

        // Then - Role should always be DRIVER
        assertThat(saved.getRole()).isEqualTo(PersonneRole.DRIVER);
    }

    @Test
    void shouldUpdateDriverVehiculeOnly() {
        // Given
        Driver savedDriver = repository.save(driver1);
        String originalFirstName = savedDriver.getFirstName();
        String originalZoneId = savedDriver.getZoneAssignee().getId();

        // When - Update only the vehicule
        savedDriver.setVehicule("Updated Vehicle");
        Driver updated = repository.save(savedDriver);

        // Then
        assertThat(updated.getVehicule()).isEqualTo("Updated Vehicle");
        assertThat(updated.getFirstName()).isEqualTo(originalFirstName);
        assertThat(updated.getZoneAssignee().getId()).isEqualTo(originalZoneId);
    }

    @Test
    void shouldUpdateDriverZoneOnly() {
        // Given
        Driver savedDriver = repository.save(driver1);
        String originalVehicule = savedDriver.getVehicule();

        // When - Update only the zone
        savedDriver.setZoneAssignee(zone2);
        Driver updated = repository.save(savedDriver);

        // Then
        assertThat(updated.getZoneAssignee().getId()).isEqualTo(zone2.getId());
        assertThat(updated.getVehicule()).isEqualTo(originalVehicule);
    }

    @Test
    void shouldFindDriverAfterMultipleUpdates() {
        // Given
        Driver savedDriver = repository.save(driver1);
        String driverId = savedDriver.getId();

        // When - Perform multiple updates
        savedDriver.setFirstName("First Update");
        repository.save(savedDriver);

        savedDriver.setEmail("first.update@example.com");
        repository.save(savedDriver);

        savedDriver.setVehicule("Final Vehicle");
        savedDriver.setZoneAssignee(zone2);
        repository.save(savedDriver);

        // Then
        Optional<Driver> found = repository.findById(driverId);
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("First Update");
        assertThat(found.get().getEmail()).isEqualTo("first.update@example.com");
        assertThat(found.get().getVehicule()).isEqualTo("Final Vehicle");
        assertThat(found.get().getZoneAssignee().getId()).isEqualTo(zone2.getId());
    }

    // Helper method to create valid Driver instances
    private Driver createDriver(String firstName, String lastName, String email,
                                String phoneNumber, String vehicule, Zone zone) {
        Driver driver = new Driver(firstName, lastName, email, phoneNumber, vehicule);
        driver.setZoneAssignee(zone);
        return driver;
    }

    // Helper method to create Zone instances
    private Zone createZone(String name, String codePostal) {
        Zone zone = new Zone();
        zone.setName(name);
        zone.setCodePostal(codePostal);
        return zone;
    }
}
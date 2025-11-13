package com.smartlogi.smartlogidms.masterdata.zone.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.check_nullability=true"
})
class ZoneRepositoryTest {

    @Autowired
    private ZoneRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Zone zone1;
    private Zone zone2;
    private Zone zone3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.clear();

        // Create test data
        zone1 = createZone("Zone Paris Center", "75001");
        zone2 = createZone("Zone Lyon Center", "69001");
        zone3 = createZone("Zone Marseille Port", "13001");
    }

    @Test
    void shouldSaveZoneSuccessfully() {
        // Given
        Zone newZone = createZone("Zone Test", "12345");

        // When
        Zone saved = repository.save(newZone);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getName()).isEqualTo("Zone Test");
        assertThat(saved.getCodePostal()).isEqualTo("12345");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindZoneById() {
        // Given
        Zone savedZone = repository.save(zone1);

        // When
        Optional<Zone> found = repository.findById(savedZone.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Zone Paris Center");
        assertThat(found.get().getCodePostal()).isEqualTo("75001");
    }

    @Test
    void shouldReturnEmptyWhenZoneNotFoundById() {
        // When
        Optional<Zone> found = repository.findById("non-existent-id");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllZones() {
        // Given
        repository.save(zone1);
        repository.save(zone2);
        repository.save(zone3);

        // When
        List<Zone> zones = repository.findAll();

        // Then
        assertThat(zones).hasSize(3);
        assertThat(zones)
                .extracting(Zone::getName)
                .containsExactlyInAnyOrder("Zone Paris Center", "Zone Lyon Center", "Zone Marseille Port");
    }

    @Test
    void shouldFindAllZonesWithPagination() {
        // Given
        repository.save(zone1);
        repository.save(zone2);
        repository.save(zone3);
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Zone> zonePage = repository.findAll(pageable);

        // Then
        assertThat(zonePage.getContent()).hasSize(2);
        assertThat(zonePage.getTotalElements()).isEqualTo(3);
        assertThat(zonePage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldUpdateZoneSuccessfully() {
        // Given
        Zone savedZone = repository.save(zone1);
        String originalId = savedZone.getId();

        // When - Update the zone
        savedZone.setName("Zone Paris Updated");
        savedZone.setCodePostal("75002");
        Zone updated = repository.save(savedZone);

        // Then
        assertThat(updated.getId()).isEqualTo(originalId);
        assertThat(updated.getName()).isEqualTo("Zone Paris Updated");
        assertThat(updated.getCodePostal()).isEqualTo("75002");
    }

    @Test
    void shouldDeleteZoneSuccessfully() {
        // Given
        Zone savedZone = repository.save(zone1);
        String zoneId = savedZone.getId();

        // When
        repository.deleteById(zoneId);
        Optional<Zone> found = repository.findById(zoneId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfZoneExistsById() {
        // Given
        Zone savedZone = repository.save(zone1);

        // When
        boolean exists = repository.existsById(savedZone.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfZoneDoesNotExistById() {
        // When
        boolean exists = repository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldCountZonesCorrectly() {
        // Given
        repository.save(zone1);
        repository.save(zone2);

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldDeleteAllZones() {
        // Given
        repository.save(zone1);
        repository.save(zone2);

        // When
        repository.deleteAll();
        List<Zone> allZones = repository.findAll();

        // Then
        assertThat(allZones).isEmpty();
    }

    @Test
    void shouldSaveAndRetrieveZoneWithSpecialCharacters() {
        // Given
        Zone zoneWithSpecialChars = createZone("Zone Paris-Île-de-France", "75000");

        // When
        Zone saved = repository.save(zoneWithSpecialChars);
        Optional<Zone> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Zone Paris-Île-de-France");
        assertThat(found.get().getCodePostal()).isEqualTo("75000");
    }

    @Test
    void shouldHandleZoneWithMaximumAllowedNameLength() {
        // Given - Name exactly at maximum length (50 characters)
        String maxLengthName = "A".repeat(50);
        Zone zoneWithMaxName = createZone(maxLengthName, "12345");

        // When
        Zone saved = repository.save(zoneWithMaxName);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).hasSize(50);
    }

    @Test
    void shouldHandleZoneWithMaximumAllowedCodePostalLength() {
        // Given - Postal code exactly at maximum length (20 characters)
        String maxLengthCodePostal = "1".repeat(20);
        Zone zoneWithMaxCodePostal = createZone("Zone Test", maxLengthCodePostal);

        // When
        Zone saved = repository.save(zoneWithMaxCodePostal);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getCodePostal()).hasSize(20);
    }

    @Test
    void shouldSaveZoneWithMinimumData() {
        // Given - Zone with minimal valid data
        Zone minimalZone = createZone("Z", "1");

        // When
        Zone saved = repository.save(minimalZone);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Z");
        assertThat(saved.getCodePostal()).isEqualTo("1");
    }

    @Test
    void shouldUpdateZoneNameOnly() {
        // Given
        Zone savedZone = repository.save(zone1);
        String originalCodePostal = savedZone.getCodePostal();

        // When - Update only the name
        savedZone.setName("Updated Name Only");
        Zone updated = repository.save(savedZone);

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Name Only");
        assertThat(updated.getCodePostal()).isEqualTo(originalCodePostal);
    }

    @Test
    void shouldUpdateZoneCodePostalOnly() {
        // Given
        Zone savedZone = repository.save(zone1);
        String originalName = savedZone.getName();

        // When - Update only the postal code
        savedZone.setCodePostal("99999");
        Zone updated = repository.save(savedZone);

        // Then
        assertThat(updated.getCodePostal()).isEqualTo("99999");
        assertThat(updated.getName()).isEqualTo(originalName);
    }

    @Test
    void shouldFindZoneAfterMultipleUpdates() {
        // Given
        Zone savedZone = repository.save(zone1);
        String zoneId = savedZone.getId();

        // When - Perform multiple updates
        savedZone.setName("First Update");
        repository.save(savedZone);

        savedZone.setCodePostal("11111");
        repository.save(savedZone);

        savedZone.setName("Final Name");
        repository.save(savedZone);

        // Then
        Optional<Zone> found = repository.findById(zoneId);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Final Name");
        assertThat(found.get().getCodePostal()).isEqualTo("11111");
    }

    // Helper method to create valid Zone instances
    private Zone createZone(String name, String codePostal) {
        Zone zone = new Zone();
        zone.setName(name);
        zone.setCodePostal(codePostal);
        return zone;
    }
}
package com.smartlogi.smartlogidms.masterdata.zone.domain;

import com.smartlogi.smartlogidms.common.specification.GenericSpecification;
import com.smartlogi.smartlogidms.common.specification.SearchCriteria;
import com.smartlogi.smartlogidms.common.specification.SearchOperation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ZoneRepositorySpecificationTest {

    @Autowired
    private ZoneRepository zoneRepository;

    @Test
    void findAll_WithNameLikeSpecification_ShouldReturnMatchingZones() {
        // Given
        Zone zone1 = createZone("Paris Center", "75001");
        Zone zone2 = createZone("Lyon Center", "69001");
        Zone zone3 = createZone("Marseille Port", "13001");
        zoneRepository.saveAll(List.of(zone1, zone2, zone3));

        GenericSpecification<Zone> spec = new GenericSpecification<>();
        spec.add(new SearchCriteria("name", "Paris", SearchOperation.LIKE));

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Zone> result = zoneRepository.findAll(spec, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Paris Center");
    }

    @Test
    void findAll_WithCodePostalEqualSpecification_ShouldReturnMatchingZones() {
        // Given
        Zone zone1 = createZone("Paris Center", "75001");
        Zone zone2 = createZone("Lyon Center", "69001");
        zoneRepository.saveAll(List.of(zone1, zone2));

        GenericSpecification<Zone> spec = new GenericSpecification<>();
        spec.add(new SearchCriteria("codePostal", "75001", SearchOperation.EQUAL));

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Zone> result = zoneRepository.findAll(spec, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCodePostal()).isEqualTo("75001");
    }

    @Test
    void findAll_WithMultipleCriteria_ShouldReturnMatchingZones() {
        // Given
        Zone zone1 = createZone("Paris Center", "75001");
        Zone zone2 = createZone("Paris Suburb", "75002");
        Zone zone3 = createZone("Lyon Center", "69001");
        zoneRepository.saveAll(List.of(zone1, zone2, zone3));

        GenericSpecification<Zone> spec = new GenericSpecification<>();
        spec.add(new SearchCriteria("name", "Paris", SearchOperation.LIKE));
        spec.add(new SearchCriteria("codePostal", "75001", SearchOperation.EQUAL));

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Zone> result = zoneRepository.findAll(spec, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Paris Center");
        assertThat(result.getContent().get(0).getCodePostal()).isEqualTo("75001");
    }

    @Test
    void findAll_WithNoMatchingCriteria_ShouldReturnEmpty() {
        // Given
        Zone zone1 = createZone("Paris Center", "75001");
        zoneRepository.save(zone1);

        GenericSpecification<Zone> spec = new GenericSpecification<>();
        spec.add(new SearchCriteria("name", "NonExistent", SearchOperation.LIKE));

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Zone> result = zoneRepository.findAll(spec, pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findAll_WithEmptySpecification_ShouldReturnAllZones() {
        // Given
        Zone zone1 = createZone("Paris Center", "75001");
        Zone zone2 = createZone("Lyon Center", "69001");
        zoneRepository.saveAll(List.of(zone1, zone2));

        GenericSpecification<Zone> spec = new GenericSpecification<>();

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Zone> result = zoneRepository.findAll(spec, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
    }

    private Zone createZone(String name, String codePostal) {
        Zone zone = new Zone();
        zone.setName(name);
        zone.setCodePostal(codePostal);
        return zone;
    }
}
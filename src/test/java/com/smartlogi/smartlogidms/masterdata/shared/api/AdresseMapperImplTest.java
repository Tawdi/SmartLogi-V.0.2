package com.smartlogi.smartlogidms.masterdata.shared.api;

import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdresseMapperImplTest {

    private AdresseMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new AdresseMapperImpl();
    }

    @Test
    void toDto_WithValidEntity_ShouldReturnCorrectDto() {
        // Given
        Adresse entity = new Adresse();
        entity.setVille("Casablanca");
        entity.setRue("123 Boulevard Mohamed V");
        entity.setCodePostal("20000");

        // When
        AdresseDTO result = mapper.toDto(entity);

        // Then
        assertNotNull(result);
        assertEquals("Casablanca", result.getVille());
        assertEquals("123 Boulevard Mohamed V", result.getRue());
        assertEquals("20000", result.getCodePostal());
    }

    @Test
    void toDto_WithNullEntity_ShouldReturnNull() {
        // Given
        Adresse entity = null;

        // When
        AdresseDTO result = mapper.toDto(entity);

        // Then
        assertNull(result);
    }

    @Test
    void toDto_WithPartialEntity_ShouldReturnPartialDto() {
        // Given
        Adresse entity = new Adresse();
        entity.setVille("Rabat");
        // rue and codePostal are null

        // When
        AdresseDTO result = mapper.toDto(entity);

        // Then
        assertNotNull(result);
        assertEquals("Rabat", result.getVille());
        assertNull(result.getRue());
        assertNull(result.getCodePostal());
    }

    @Test
    void entitiesToResponseDtos_WithValidEntities_ShouldReturnCorrectDtos() {
        // Given
        Adresse entity1 = new Adresse();
        entity1.setVille("Casablanca");
        entity1.setRue("Rue 1");
        entity1.setCodePostal("20000");

        Adresse entity2 = new Adresse();
        entity2.setVille("Rabat");
        entity2.setRue("Rue 2");
        entity2.setCodePostal("10000");

        List<Adresse> entities = Arrays.asList(entity1, entity2);

        // When
        List<AdresseDTO> result = mapper.entitiesToResponseDtos(entities);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        AdresseDTO dto1 = result.get(0);
        assertEquals("Casablanca", dto1.getVille());
        assertEquals("Rue 1", dto1.getRue());
        assertEquals("20000", dto1.getCodePostal());

        AdresseDTO dto2 = result.get(1);
        assertEquals("Rabat", dto2.getVille());
        assertEquals("Rue 2", dto2.getRue());
        assertEquals("10000", dto2.getCodePostal());
    }

    @Test
    void entitiesToResponseDtos_WithNullList_ShouldReturnNull() {
        // Given
        List<Adresse> entities = null;

        // When
        List<AdresseDTO> result = mapper.entitiesToResponseDtos(entities);

        // Then
        assertNull(result);
    }

    @Test
    void entitiesToResponseDtos_WithEmptyList_ShouldReturnEmptyList() {
        // Given
        List<Adresse> entities = Arrays.asList();

        // When
        List<AdresseDTO> result = mapper.entitiesToResponseDtos(entities);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toEntity_WithValidDto_ShouldReturnCorrectEntity() {
        // Given
        AdresseDTO dto = new AdresseDTO();
        dto.setVille("Marrakech");
        dto.setRue("Avenue Hassan II");
        dto.setCodePostal("40000");

        // When
        Adresse result = mapper.toEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals("Marrakech", result.getVille());
        assertEquals("Avenue Hassan II", result.getRue());
        assertEquals("40000", result.getCodePostal());
    }

    @Test
    void toEntity_WithNullDto_ShouldReturnNull() {
        // Given
        AdresseDTO dto = null;

        // When
        Adresse result = mapper.toEntity(dto);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_WithPartialDto_ShouldReturnPartialEntity() {
        // Given
        AdresseDTO dto = new AdresseDTO();
        dto.setVille("Tanger");
        // rue and codePostal are null

        // When
        Adresse result = mapper.toEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals("Tanger", result.getVille());
        assertNull(result.getRue());
        assertNull(result.getCodePostal());
    }

    @Test
    void updateEntityFromDto_WithValidDto_ShouldUpdateEntity() {
        // Given
        Adresse entity = new Adresse();
        entity.setVille("Old City");
        entity.setRue("Old Street");
        entity.setCodePostal("00000");

        AdresseDTO dto = new AdresseDTO();
        dto.setVille("New City");
        dto.setRue("New Street");
        dto.setCodePostal("11111");

        // When
        mapper.updateEntityFromDto(dto, entity);

        // Then
        assertEquals("New City", entity.getVille());
        assertEquals("New Street", entity.getRue());
        assertEquals("11111", entity.getCodePostal());
    }

    @Test
    void updateEntityFromDto_WithNullDto_ShouldNotUpdateEntity() {
        // Given
        Adresse entity = new Adresse();
        entity.setVille("Original City");
        entity.setRue("Original Street");
        entity.setCodePostal("22222");

        AdresseDTO dto = null;

        // When
        mapper.updateEntityFromDto(dto, entity);

        // Then - Entity should remain unchanged
        assertEquals("Original City", entity.getVille());
        assertEquals("Original Street", entity.getRue());
        assertEquals("22222", entity.getCodePostal());
    }

    @Test
    void updateEntityFromDto_WithPartialDto_ShouldUpdateOnlyProvidedFields() {
        // Given
        Adresse entity = new Adresse();
        entity.setVille("Original City");
        entity.setRue("Original Street");
        entity.setCodePostal("22222");

        AdresseDTO dto = new AdresseDTO();
        dto.setVille("Updated City");
        // rue and codePostal are null

        // When
        mapper.updateEntityFromDto(dto, entity);

        // Then
        assertEquals("Updated City", entity.getVille());
        assertNull(entity.getRue()); // Should be updated to null
        assertNull(entity.getCodePostal()); // Should be updated to null
    }

    @Test
    void updateEntityFromDto_WithEmptyStrings_ShouldUpdateWithEmptyStrings() {
        // Given
        Adresse entity = new Adresse();
        entity.setVille("Original City");
        entity.setRue("Original Street");
        entity.setCodePostal("22222");

        AdresseDTO dto = new AdresseDTO();
        dto.setVille("");
        dto.setRue("");
        dto.setCodePostal("");

        // When
        mapper.updateEntityFromDto(dto, entity);

        // Then
        assertEquals("", entity.getVille());
        assertEquals("", entity.getRue());
        assertEquals("", entity.getCodePostal());
    }

    @Test
    void roundTripTest_EntityToDtoToEntity_ShouldPreserveData() {
        // Given
        Adresse originalEntity = new Adresse();
        originalEntity.setVille("Fès");
        originalEntity.setRue("Rue du Vieux Fès");
        originalEntity.setCodePostal("30000");

        // When
        AdresseDTO dto = mapper.toDto(originalEntity);
        Adresse roundTripEntity = mapper.toEntity(dto);

        // Then
        assertNotNull(roundTripEntity);
        assertEquals(originalEntity.getVille(), roundTripEntity.getVille());
        assertEquals(originalEntity.getRue(), roundTripEntity.getRue());
        assertEquals(originalEntity.getCodePostal(), roundTripEntity.getCodePostal());
    }

    @Test
    void roundTripTest_DtoToEntityToDto_ShouldPreserveData() {
        // Given
        AdresseDTO originalDto = new AdresseDTO();
        originalDto.setVille("Meknès");
        originalDto.setRue("Avenue Mohammed VI");
        originalDto.setCodePostal("50000");

        // When
        Adresse entity = mapper.toEntity(originalDto);
        AdresseDTO roundTripDto = mapper.toDto(entity);

        // Then
        assertNotNull(roundTripDto);
        assertEquals(originalDto.getVille(), roundTripDto.getVille());
        assertEquals(originalDto.getRue(), roundTripDto.getRue());
        assertEquals(originalDto.getCodePostal(), roundTripDto.getCodePostal());
    }
}
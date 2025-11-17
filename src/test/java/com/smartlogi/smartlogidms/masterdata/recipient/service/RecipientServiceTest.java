package com.smartlogi.smartlogidms.masterdata.recipient.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientMapper;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientMapperImpl;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.api.RecipientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.Recipient;
import com.smartlogi.smartlogidms.masterdata.recipient.domain.RecipientRepository;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientServiceImplTest {

    @Mock
    private RecipientRepository repository;

    // Use real mapper implementation
    @Spy
    private RecipientMapper mapper = new RecipientMapperImpl();

    private RecipientServiceImpl recipientService;

    private Recipient recipientEntity;
    private RecipientRequestDTO recipientRequestDTO;

    @BeforeEach
    void setUp() {
        recipientService = new RecipientServiceImpl(repository, mapper);

        // Setup test data
        recipientEntity = new Recipient();
        recipientEntity.setId("RECIPIENT-123");
        recipientEntity.setFirstName("Alice");
        recipientEntity.setLastName("Johnson");
        recipientEntity.setEmail("alice.johnson@example.com");
        recipientEntity.setPhoneNumber("+1234567890");
        recipientEntity.setAdresse(new Adresse("Paris", "123 Main Street", "75001"));

        recipientRequestDTO = new RecipientRequestDTO();
        recipientRequestDTO.setFirstName("Alice");
        recipientRequestDTO.setLastName("Johnson");
        recipientRequestDTO.setEmail("alice.johnson@example.com");
        recipientRequestDTO.setPhoneNumber("+1234567890");
        recipientRequestDTO.setRue("123 Main Street");
        recipientRequestDTO.setVille("Paris");
        recipientRequestDTO.setCodePostal("75001");
    }

    @Test
    void shouldSaveRecipientSuccessfully() {
        // Given
        when(repository.save(any(Recipient.class))).thenReturn(recipientEntity);

        // When
        RecipientResponseDTO result = recipientService.save(recipientRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getLastName()).isEqualTo("Johnson");
        assertThat(result.getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(result.getRue()).isEqualTo("123 Main Street");
        assertThat(result.getVille()).isEqualTo("Paris");
        assertThat(result.getCodePostal()).isEqualTo("75001");
        // Real mapper ensures proper mapping of address fields from DTO to embedded Adresse

        verify(repository).save(any(Recipient.class));
    }

    @Test
    void shouldUpdateRecipientSuccessfully() {
        // Given
        String recipientId = "RECIPIENT-123";
        RecipientRequestDTO updateRequest = new RecipientRequestDTO();
        updateRequest.setFirstName("Alice Updated");
        updateRequest.setLastName("Johnson Updated");
        updateRequest.setEmail("alice.updated@example.com");
        updateRequest.setPhoneNumber("+0987654321");
        updateRequest.setRue("456 Updated Street");
        updateRequest.setVille("Lyon");
        updateRequest.setCodePostal("69001");

        Recipient existingRecipient = new Recipient();
        existingRecipient.setId(recipientId);
        existingRecipient.setFirstName("Alice");
        existingRecipient.setLastName("Johnson");
        existingRecipient.setEmail("alice.johnson@example.com");
        existingRecipient.setPhoneNumber("+1234567890");
        existingRecipient.setAdresse(new Adresse("Paris", "123 Main Street", "75001"));

        when(repository.findById(recipientId)).thenReturn(Optional.of(existingRecipient));
        when(repository.save(any(Recipient.class))).thenReturn(existingRecipient);

        // When
        RecipientResponseDTO result = recipientService.update(recipientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(recipientId);
        assertThat(result.getFirstName()).isEqualTo("Alice Updated");
        assertThat(result.getLastName()).isEqualTo("Johnson Updated");
        assertThat(result.getEmail()).isEqualTo("alice.updated@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+0987654321");
        assertThat(result.getRue()).isEqualTo("456 Updated Street");
        assertThat(result.getVille()).isEqualTo("Lyon");
        assertThat(result.getCodePostal()).isEqualTo("69001");
        // Real mapper ensures proper mapping of updated address fields

        verify(repository).findById(recipientId);
        verify(repository).save(existingRecipient);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentRecipient() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        RecipientRequestDTO updateRequest = new RecipientRequestDTO();
        updateRequest.setFirstName("Test");
        updateRequest.setLastName("Recipient");

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recipientService.update(nonExistentId, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).findById(nonExistentId);
        verify(repository, never()).save(any(Recipient.class));
    }

    @Test
    void shouldFindRecipientByIdSuccessfully() {
        // Given
        String recipientId = "RECIPIENT-123";
        when(repository.findById(recipientId)).thenReturn(Optional.of(recipientEntity));

        // When
        RecipientResponseDTO result = recipientService.findById(recipientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(recipientId);
        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getLastName()).isEqualTo("Johnson");
        assertThat(result.getEmail()).isEqualTo("alice.johnson@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(result.getRue()).isEqualTo("123 Main Street");
        assertThat(result.getVille()).isEqualTo("Paris");
        assertThat(result.getCodePostal()).isEqualTo("75001");
        // Real mapper properly maps embedded Adresse object to DTO fields

        verify(repository).findById(recipientId);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentRecipientById() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recipientService.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).findById(nonExistentId);
    }

    @Test
    void shouldFindRecipientByEmailSuccessfully() {
        // Given
        String email = "alice.johnson@example.com";
        when(repository.findByEmail(email)).thenReturn(Optional.of(recipientEntity));

        // When
        RecipientResponseDTO result = recipientService.findByEmail(email);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getLastName()).isEqualTo("Johnson");
        assertThat(result.getRue()).isEqualTo("123 Main Street");
        assertThat(result.getVille()).isEqualTo("Paris");
        assertThat(result.getCodePostal()).isEqualTo("75001");
        // Real mapper ensures all address fields are properly mapped from embedded Adresse

        verify(repository).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentRecipientByEmail() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(repository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> recipientService.findByEmail(nonExistentEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with email: " + nonExistentEmail);

        verify(repository).findByEmail(nonExistentEmail);
    }

    @Test
    void shouldFindAllRecipients() {
        // Given
        List<Recipient> recipients = List.of(recipientEntity);
        when(repository.findAll()).thenReturn(recipients);

        // When
        List<RecipientResponseDTO> result = recipientService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("RECIPIENT-123");
        assertThat(result.get(0).getFirstName()).isEqualTo("Alice");
        assertThat(result.get(0).getLastName()).isEqualTo("Johnson");
        assertThat(result.get(0).getRue()).isEqualTo("123 Main Street");
        assertThat(result.get(0).getVille()).isEqualTo("Paris");
        assertThat(result.get(0).getCodePostal()).isEqualTo("75001");
        // Real mapper ensures proper mapping in list results

        verify(repository).findAll();
    }

    @Test
    void shouldFindAllRecipientsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recipient> recipientPage = new PageImpl<>(List.of(recipientEntity), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(recipientPage);

        // When
        Page<RecipientResponseDTO> result = recipientService.findAll(pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("RECIPIENT-123");
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Alice");
        assertThat(result.getContent().get(0).getRue()).isEqualTo("123 Main Street");
        assertThat(result.getTotalElements()).isEqualTo(1);
        // Real mapper ensures proper mapping in paginated results

        verify(repository).findAll(pageable);
    }

    @Test
    void shouldDeleteRecipientSuccessfully() {
        // Given
        String recipientId = "RECIPIENT-123";
        when(repository.existsById(recipientId)).thenReturn(true);
        doNothing().when(repository).deleteById(recipientId);

        // When
        recipientService.deleteById(recipientId);

        // Then
        verify(repository).existsById(recipientId);
        verify(repository).deleteById(recipientId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentRecipient() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        when(repository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> recipientService.deleteById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).existsById(nonExistentId);
        verify(repository, never()).deleteById(nonExistentId);
    }

    @Test
    void shouldCheckIfRecipientExists() {
        // Given
        String recipientId = "RECIPIENT-123";
        when(repository.existsById(recipientId)).thenReturn(true);

        // When
        boolean exists = recipientService.existsById(recipientId);

        // Then
        assertThat(exists).isTrue();
        verify(repository).existsById(recipientId);
    }

    @Test
    void shouldCheckIfRecipientDoesNotExist() {
        // Given
        String recipientId = "NON-EXISTENT";
        when(repository.existsById(recipientId)).thenReturn(false);

        // When
        boolean exists = recipientService.existsById(recipientId);

        // Then
        assertThat(exists).isFalse();
        verify(repository).existsById(recipientId);
    }

    @Test
    void shouldFindAllRecipientsWithSpecification() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.jpa.domain.Specification<Recipient> spec =
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ville"), "Paris");

        Page<Recipient> recipientPage = new PageImpl<>(List.of(recipientEntity), pageable, 1);

        when(repository.findAll(spec, pageable)).thenReturn(recipientPage);

        // When
        Page<RecipientResponseDTO> result = recipientService.findAll(pageable, spec);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getVille()).isEqualTo("Paris");
        assertThat(result.getContent().get(0).getRue()).isEqualTo("123 Main Street");
        assertThat(result.getTotalElements()).isEqualTo(1);
        // Real mapper ensures proper mapping with specification

        verify(repository).findAll(spec, pageable);
    }

    @Test
    void shouldMapRecipientWithoutAddressCorrectly() {
        // Given
        Recipient recipientWithoutAddress = new Recipient();
        recipientWithoutAddress.setId("RECIPIENT-NO-ADDRESS");
        recipientWithoutAddress.setFirstName("Bob");
        recipientWithoutAddress.setLastName("Smith");
        recipientWithoutAddress.setEmail("bob.smith@example.com");
        recipientWithoutAddress.setPhoneNumber("+1234567891");
        // No address set

        when(repository.findById("RECIPIENT-NO-ADDRESS")).thenReturn(Optional.of(recipientWithoutAddress));

        // When
        RecipientResponseDTO result = recipientService.findById("RECIPIENT-NO-ADDRESS");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("RECIPIENT-NO-ADDRESS");
        assertThat(result.getFirstName()).isEqualTo("Bob");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getRue()).isNull(); // Should be null when no address
        assertThat(result.getVille()).isNull(); // Should be null when no address
        assertThat(result.getCodePostal()).isNull(); // Should be null when no address
        // Real mapper handles null address correctly
    }

    @Test
    void shouldMapRecipientWithPartialAddress() {
        // Given
        Recipient recipientWithPartialAddress = new Recipient();
        recipientWithPartialAddress.setId("RECIPIENT-PARTIAL-ADDRESS");
        recipientWithPartialAddress.setFirstName("Carol");
        recipientWithPartialAddress.setLastName("Davis");
        recipientWithPartialAddress.setEmail("carol.davis@example.com");
        recipientWithPartialAddress.setPhoneNumber("+1234567892");
        recipientWithPartialAddress.setAdresse(new Adresse("Lyon", null, "69001")); // Rue is null

        when(repository.findById("RECIPIENT-PARTIAL-ADDRESS")).thenReturn(Optional.of(recipientWithPartialAddress));

        // When
        RecipientResponseDTO result = recipientService.findById("RECIPIENT-PARTIAL-ADDRESS");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("RECIPIENT-PARTIAL-ADDRESS");
        assertThat(result.getFirstName()).isEqualTo("Carol");
        assertThat(result.getLastName()).isEqualTo("Davis");
        assertThat(result.getRue()).isNull(); // Should be null when address rue is null
        assertThat(result.getVille()).isEqualTo("Lyon");
        assertThat(result.getCodePostal()).isEqualTo("69001");
        // Real mapper handles partial address correctly
    }

    @Test
    void shouldUpdateRecipientWithPartialAddress() {
        // Given
        String recipientId = "RECIPIENT-123";
        RecipientRequestDTO updateRequest = new RecipientRequestDTO();
        updateRequest.setFirstName("Alice");
        updateRequest.setLastName("Johnson");
        updateRequest.setEmail("alice.johnson@example.com");
        updateRequest.setPhoneNumber("+1234567890");
        updateRequest.setRue(null); // Only updating ville and codePostal
        updateRequest.setVille("Nice");
        updateRequest.setCodePostal("06000");

        Recipient existingRecipient = new Recipient();
        existingRecipient.setId(recipientId);
        existingRecipient.setFirstName("Alice");
        existingRecipient.setLastName("Johnson");
        existingRecipient.setEmail("alice.johnson@example.com");
        existingRecipient.setPhoneNumber("+1234567890");
        existingRecipient.setAdresse(new Adresse("Paris", "123 Main Street", "75001"));

        when(repository.findById(recipientId)).thenReturn(Optional.of(existingRecipient));
        when(repository.save(any(Recipient.class))).thenReturn(existingRecipient);

        // When
        RecipientResponseDTO result = recipientService.update(recipientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getVille()).isEqualTo("Nice");
        assertThat(result.getCodePostal()).isEqualTo("06000");
        // Real mapper handles partial address updates correctly
        // Note: The behavior depends on your mapper implementation - whether it sets null values or ignores them

        verify(repository).findById(recipientId);
        verify(repository).save(existingRecipient);
    }

    @Test
    void shouldSaveRecipientWithMinimalAddress() {
        // Given
        RecipientRequestDTO minimalRequest = new RecipientRequestDTO();
        minimalRequest.setFirstName("David");
        minimalRequest.setLastName("Wilson");
        minimalRequest.setEmail("david.wilson@example.com");
        minimalRequest.setPhoneNumber("+1234567893");
        minimalRequest.setRue(null); // Minimal address
        minimalRequest.setVille("Marseille");
        minimalRequest.setCodePostal("13001");

        Recipient savedRecipient = new Recipient();
        savedRecipient.setId("RECIPIENT-MINIMAL");
        savedRecipient.setFirstName("David");
        savedRecipient.setLastName("Wilson");
        savedRecipient.setEmail("david.wilson@example.com");
        savedRecipient.setPhoneNumber("+1234567893");
        savedRecipient.setAdresse(new Adresse("Marseille", null, "13001"));

        when(repository.save(any(Recipient.class))).thenReturn(savedRecipient);

        // When
        RecipientResponseDTO result = recipientService.save(minimalRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("David");
        assertThat(result.getLastName()).isEqualTo("Wilson");
        assertThat(result.getVille()).isEqualTo("Marseille");
        assertThat(result.getCodePostal()).isEqualTo("13001");
        assertThat(result.getRue()).isNull(); // Should be null when not provided
        // Real mapper handles minimal address correctly

        verify(repository).save(any(Recipient.class));
    }

    @Test
    void shouldHandleMultipleRecipientsWithDifferentAddresses() {
        // Given
        Recipient recipient1 = new Recipient();
        recipient1.setId("RECIPIENT-1");
        recipient1.setFirstName("John");
        recipient1.setLastName("Doe");
        recipient1.setEmail("john.doe@example.com");
        recipient1.setPhoneNumber("+1111111111");
        recipient1.setAdresse(new Adresse("Paris", "Street 1", "75001"));

        Recipient recipient2 = new Recipient();
        recipient2.setId("RECIPIENT-2");
        recipient2.setFirstName("Jane");
        recipient2.setLastName("Smith");
        recipient2.setEmail("jane.smith@example.com");
        recipient2.setPhoneNumber("+2222222222");
        recipient2.setAdresse(new Adresse("Lyon", "Street 2", "69001"));

        List<Recipient> recipients = List.of(recipient1, recipient2);
        when(repository.findAll()).thenReturn(recipients);

        // When
        List<RecipientResponseDTO> result = recipientService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(RecipientResponseDTO::getVille)
                .containsExactly("Paris", "Lyon");
        assertThat(result)
                .extracting(RecipientResponseDTO::getRue)
                .containsExactly("Street 1", "Street 2");
        assertThat(result)
                .extracting(RecipientResponseDTO::getCodePostal)
                .containsExactly("75001", "69001");
        // Real mapper ensures all recipients are properly mapped with their addresses

        verify(repository).findAll();
    }
}
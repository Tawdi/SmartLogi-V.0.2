package com.smartlogi.smartlogidms.masterdata.client.service;

import com.smartlogi.smartlogidms.common.exception.ResourceNotFoundException;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientMapper;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientMapperImpl;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientRequestDTO;
import com.smartlogi.smartlogidms.masterdata.client.api.ClientResponseDTO;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteur;
import com.smartlogi.smartlogidms.masterdata.client.domain.ClientExpediteurRepository;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientExpediteurRepository repository;

    // Use real mapper implementation
    @Spy
    private ClientMapper mapper = new ClientMapperImpl();

    private ClientServiceImpl clientService;

    private ClientExpediteur clientEntity;
    private ClientRequestDTO clientRequestDTO;

    @BeforeEach
    void setUp() {
        clientService = new ClientServiceImpl(repository, mapper);

        // Setup test data
        clientEntity = new ClientExpediteur();
        clientEntity.setId("CLIENT-123");
        clientEntity.setFirstName("John");
        clientEntity.setLastName("Doe");
        clientEntity.setEmail("john.doe@example.com");
        clientEntity.setPhoneNumber("+1234567890");
        clientEntity.setAdresse(new Adresse("Paris", "123 Main Street", "75001"));

        clientRequestDTO = new ClientRequestDTO();
        clientRequestDTO.setFirstName("John");
        clientRequestDTO.setLastName("Doe");
        clientRequestDTO.setEmail("john.doe@example.com");
        clientRequestDTO.setPhoneNumber("+1234567890");
        clientRequestDTO.setRue("123 Main Street");
        clientRequestDTO.setVille("Paris");
        clientRequestDTO.setCodePostal("75001");
    }

    @Test
    void shouldSaveClientSuccessfully() {
        // Given
        when(repository.save(any(ClientExpediteur.class))).thenReturn(clientEntity);

        // When
        ClientResponseDTO result = clientService.save(clientRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(result.getRue()).isEqualTo("123 Main Street");
        assertThat(result.getVille()).isEqualTo("Paris");
        assertThat(result.getCodePostal()).isEqualTo("75001");
        // Real mapper ensures proper mapping of address fields

        verify(repository).save(any(ClientExpediteur.class));
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        // Given
        String clientId = "CLIENT-123";
        ClientRequestDTO updateRequest = new ClientRequestDTO();
        updateRequest.setFirstName("John Updated");
        updateRequest.setLastName("Doe Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setPhoneNumber("+0987654321");
        updateRequest.setRue("456 Updated Street");
        updateRequest.setVille("Lyon");
        updateRequest.setCodePostal("69001");

        ClientExpediteur existingClient = new ClientExpediteur();
        existingClient.setId(clientId);
        existingClient.setFirstName("John");
        existingClient.setLastName("Doe");
        existingClient.setEmail("john.doe@example.com");
        existingClient.setPhoneNumber("+1234567890");
        existingClient.setAdresse(new Adresse("123 Main Street", "Paris", "75001"));

        when(repository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(repository.save(any(ClientExpediteur.class))).thenReturn(existingClient);

        // When
        ClientResponseDTO result = clientService.update(clientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clientId);
        assertThat(result.getFirstName()).isEqualTo("John Updated");
        assertThat(result.getLastName()).isEqualTo("Doe Updated");
        assertThat(result.getEmail()).isEqualTo("john.updated@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+0987654321");
        assertThat(result.getRue()).isEqualTo("456 Updated Street");
        assertThat(result.getVille()).isEqualTo("Lyon");
        assertThat(result.getCodePostal()).isEqualTo("69001");
        // Real mapper ensures proper mapping of updated fields

        verify(repository).findById(clientId);
        verify(repository).save(existingClient);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentClient() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        ClientRequestDTO updateRequest = new ClientRequestDTO();
        updateRequest.setFirstName("Test");
        updateRequest.setLastName("User");

        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.update(nonExistentId, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).findById(nonExistentId);
        verify(repository, never()).save(any(ClientExpediteur.class));
    }

    @Test
    void shouldFindClientByIdSuccessfully() {
        // Given
        String clientId = "CLIENT-123";
        when(repository.findById(clientId)).thenReturn(Optional.of(clientEntity));

        // When
        ClientResponseDTO result = clientService.findById(clientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clientId);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(result.getRue()).isEqualTo("123 Main Street");
        assertThat(result.getVille()).isEqualTo("Paris");
        assertThat(result.getCodePostal()).isEqualTo("75001");
        // Real mapper properly maps address from Adresse object to DTO fields

        verify(repository).findById(clientId);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentClientById() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        when(repository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.findById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).findById(nonExistentId);
    }

    @Test
    void shouldFindAllClients() {
        // Given
        List<ClientExpediteur> clients = List.of(clientEntity);
        when(repository.findAll()).thenReturn(clients);

        // When
        List<ClientResponseDTO> result = clientService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("CLIENT-123");
        assertThat(result.get(0).getFirstName()).isEqualTo("John");
        assertThat(result.get(0).getLastName()).isEqualTo("Doe");
        assertThat(result.get(0).getRue()).isEqualTo("123 Main Street");
        assertThat(result.get(0).getVille()).isEqualTo("Paris");
        assertThat(result.get(0).getCodePostal()).isEqualTo("75001");
        // Real mapper ensures proper mapping in list results

        verify(repository).findAll();
    }

    @Test
    void shouldFindAllClientsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClientExpediteur> clientPage = new PageImpl<>(List.of(clientEntity), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(clientPage);

        // When
        Page<ClientResponseDTO> result = clientService.findAll(pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo("CLIENT-123");
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
        assertThat(result.getContent().get(0).getRue()).isEqualTo("123 Main Street");
        assertThat(result.getTotalElements()).isEqualTo(1);
        // Real mapper ensures proper mapping in paginated results

        verify(repository).findAll(pageable);
    }

    @Test
    void shouldFindClientByEmailSuccessfully() {
        // Given
        String email = "john.doe@example.com";
        when(repository.findByEmail(email)).thenReturn(Optional.of(clientEntity));

        // When
        ClientResponseDTO result = clientService.findByEmail(email);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getRue()).isEqualTo("123 Main Street");
        assertThat(result.getVille()).isEqualTo("Paris");
        assertThat(result.getCodePostal()).isEqualTo("75001");
        // Real mapper ensures all address fields are properly mapped

        verify(repository).findByEmail(email);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistentClientByEmail() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";
        when(repository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.findByEmail(nonExistentEmail))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with email: " + nonExistentEmail);

        verify(repository).findByEmail(nonExistentEmail);
    }

    @Test
    void shouldSearchClientsByKeyword() {
        // Given
        String keyword = "john";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClientExpediteur> clientPage = new PageImpl<>(List.of(clientEntity), pageable, 1);

        when(repository.searchClients(keyword, pageable)).thenReturn(clientPage);

        // When
        Page<ClientResponseDTO> result = clientService.searchClients(keyword, pageable);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Doe");
        assertThat(result.getContent().get(0).getRue()).isEqualTo("123 Main Street");
        assertThat(result.getTotalElements()).isEqualTo(1);
        // Real mapper ensures proper mapping in search results

        verify(repository).searchClients(keyword, pageable);
    }

    @Test
    void shouldDeleteClientSuccessfully() {
        // Given
        String clientId = "CLIENT-123";
        when(repository.existsById(clientId)).thenReturn(true);
        doNothing().when(repository).deleteById(clientId);

        // When
        clientService.deleteById(clientId);

        // Then
        verify(repository).existsById(clientId);
        verify(repository).deleteById(clientId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentClient() {
        // Given
        String nonExistentId = "NON-EXISTENT";
        when(repository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> clientService.deleteById(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found with id: " + nonExistentId);

        verify(repository).existsById(nonExistentId);
        verify(repository, never()).deleteById(nonExistentId);
    }

    @Test
    void shouldCheckIfClientExists() {
        // Given
        String clientId = "CLIENT-123";
        when(repository.existsById(clientId)).thenReturn(true);

        // When
        boolean exists = clientService.existsById(clientId);

        // Then
        assertThat(exists).isTrue();
        verify(repository).existsById(clientId);
    }

    @Test
    void shouldCheckIfClientDoesNotExist() {
        // Given
        String clientId = "NON-EXISTENT";
        when(repository.existsById(clientId)).thenReturn(false);

        // When
        boolean exists = clientService.existsById(clientId);

        // Then
        assertThat(exists).isFalse();
        verify(repository).existsById(clientId);
    }

    @Test
    void shouldFindAllClientsWithSpecification() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        org.springframework.data.jpa.domain.Specification<ClientExpediteur> spec =
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("ville"), "Paris");

        Page<ClientExpediteur> clientPage = new PageImpl<>(List.of(clientEntity), pageable, 1);

        when(repository.findAll(spec, pageable)).thenReturn(clientPage);

        // When
        Page<ClientResponseDTO> result = clientService.findAll(pageable, spec);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getVille()).isEqualTo("Paris");
        assertThat(result.getContent().get(0).getRue()).isEqualTo("123 Main Street");
        assertThat(result.getTotalElements()).isEqualTo(1);
        // Real mapper ensures proper mapping with specification

        verify(repository).findAll(spec, pageable);
    }

    @Test
    void shouldHandleEmptySearchResults() {
        // Given
        String keyword = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<ClientExpediteur> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(repository.searchClients(keyword, pageable)).thenReturn(emptyPage);

        // When
        Page<ClientResponseDTO> result = clientService.searchClients(keyword, pageable);

        // Then
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(repository).searchClients(keyword, pageable);
    }

    @Test
    void shouldHandleMultipleClientsInSearch() {
        // Given
        String keyword = "doe";
        Pageable pageable = PageRequest.of(0, 10);

        ClientExpediteur client2 = new ClientExpediteur();
        client2.setId("CLIENT-456");
        client2.setFirstName("Jane");
        client2.setLastName("Doe");
        client2.setEmail("jane.doe@example.com");
        client2.setPhoneNumber("+1234567891");
        client2.setAdresse(new Adresse("Lyon", "456 Oak Avenue", "69001"));

        List<ClientExpediteur> clients = List.of(clientEntity, client2);
        Page<ClientExpediteur> clientPage = new PageImpl<>(clients, pageable, 2);

        when(repository.searchClients(keyword, pageable)).thenReturn(clientPage);

        // When
        Page<ClientResponseDTO> result = clientService.searchClients(keyword, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(ClientResponseDTO::getLastName)
                .containsExactly("Doe", "Doe");
        assertThat(result.getContent())
                .extracting(ClientResponseDTO::getRue)
                .containsExactly("123 Main Street", "456 Oak Avenue");
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldMapClientWithoutAddressCorrectly() {
        // Given
        ClientExpediteur clientWithoutAddress = new ClientExpediteur();
        clientWithoutAddress.setId("CLIENT-NO-ADDRESS");
        clientWithoutAddress.setFirstName("Alice");
        clientWithoutAddress.setLastName("Smith");
        clientWithoutAddress.setEmail("alice.smith@example.com");
        clientWithoutAddress.setPhoneNumber("+1234567892");
        // No address set

        when(repository.findById("CLIENT-NO-ADDRESS")).thenReturn(Optional.of(clientWithoutAddress));

        // When
        ClientResponseDTO result = clientService.findById("CLIENT-NO-ADDRESS");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("CLIENT-NO-ADDRESS");
        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getRue()).isNull(); // Should be null when no address
        assertThat(result.getVille()).isNull(); // Should be null when no address
        assertThat(result.getCodePostal()).isNull(); // Should be null when no address
        // Real mapper handles null address correctly
    }

    @Test
    void shouldMapClientWithPartialAddress() {
        // Given
        ClientExpediteur clientWithPartialAddress = new ClientExpediteur();
        clientWithPartialAddress.setId("CLIENT-PARTIAL-ADDRESS");
        clientWithPartialAddress.setFirstName("Bob");
        clientWithPartialAddress.setLastName("Johnson");
        clientWithPartialAddress.setEmail("bob.johnson@example.com");
        clientWithPartialAddress.setPhoneNumber("+1234567893");
        clientWithPartialAddress.setAdresse(new Adresse("Marseille", null, "13001")); // Rue is null

        when(repository.findById("CLIENT-PARTIAL-ADDRESS")).thenReturn(Optional.of(clientWithPartialAddress));

        // When
        ClientResponseDTO result = clientService.findById("CLIENT-PARTIAL-ADDRESS");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("CLIENT-PARTIAL-ADDRESS");
        assertThat(result.getFirstName()).isEqualTo("Bob");
        assertThat(result.getRue()).isNull(); // Should be null when address rue is null
        assertThat(result.getVille()).isEqualTo("Marseille");
        assertThat(result.getCodePostal()).isEqualTo("13001");
        // Real mapper handles partial address correctly
    }

    @Test
    void shouldUpdateClientWithPartialAddress() {
        // Given
        String clientId = "CLIENT-123";
        ClientRequestDTO updateRequest = new ClientRequestDTO();
        updateRequest.setFirstName("John");
        updateRequest.setLastName("Doe");
        updateRequest.setEmail("john.doe@example.com");
        updateRequest.setPhoneNumber("+1234567890");
        updateRequest.setRue(null); // Only updating ville and codePostal
        updateRequest.setVille("Nice");
        updateRequest.setCodePostal("06000");

        ClientExpediteur existingClient = new ClientExpediteur();
        existingClient.setId(clientId);
        existingClient.setFirstName("John");
        existingClient.setLastName("Doe");
        existingClient.setEmail("john.doe@example.com");
        existingClient.setPhoneNumber("+1234567890");
        existingClient.setAdresse(new Adresse("Paris", "123 Main Street", "75001"));

        when(repository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(repository.save(any(ClientExpediteur.class))).thenReturn(existingClient);

        // When
        ClientResponseDTO result = clientService.update(clientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getVille()).isEqualTo("Nice");
        assertThat(result.getCodePostal()).isEqualTo("06000");
        // Real mapper handles partial address updates correctly
        // Note: The behavior depends on your mapper implementation - whether it sets null values or ignores them

        verify(repository).findById(clientId);
        verify(repository).save(existingClient);
    }
}
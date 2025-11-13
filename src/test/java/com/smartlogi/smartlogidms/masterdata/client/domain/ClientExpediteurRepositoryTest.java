package com.smartlogi.smartlogidms.masterdata.client.domain;

import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne.PersonneRole;
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
class ClientExpediteurRepositoryTest {

    @Autowired
    private ClientExpediteurRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private ClientExpediteur client1;
    private ClientExpediteur client2;
    private ClientExpediteur client3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.clear();

        // Create test data
        client1 = createClientExpediteur(
                "John", "Doe", "john.doe@example.com", "+1234567890",
                new Adresse("Paris", "123 Main St", "75001")
        );

        client2 = createClientExpediteur(
                "Jane", "Smith", "jane.smith@example.com", "+0987654321",
                new Adresse("Lyon", "456 Oak Ave", "69001")
        );

        client3 = createClientExpediteur(
                "Bob", "Johnson", "bob.johnson@example.com", "+1122334455",
                new Adresse("Marseille", "789 Pine Rd", "13001")
        );
    }

    @Test
    void shouldSaveClientExpediteurSuccessfully() {
        // Given
        ClientExpediteur newClient = createClientExpediteur(
                "Alice", "Brown", "alice.brown@example.com", "+5566778899",
                new Adresse("Toulouse", "321 Elm St", "31000")
        );

        // When
        ClientExpediteur saved = repository.save(newClient);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotBlank();
        assertThat(saved.getFirstName()).isEqualTo("Alice");
        assertThat(saved.getLastName()).isEqualTo("Brown");
        assertThat(saved.getEmail()).isEqualTo("alice.brown@example.com");
        assertThat(saved.getPhoneNumber()).isEqualTo("+5566778899");
        assertThat(saved.getRole()).isEqualTo(PersonneRole.CLIENT);
        assertThat(saved.getAdresse()).isNotNull();
        assertThat(saved.getAdresse().getVille()).isEqualTo("Toulouse");
        assertThat(saved.getAdresse().getRue()).isEqualTo("321 Elm St");
        assertThat(saved.getAdresse().getCodePostal()).isEqualTo("31000");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindClientExpediteurById() {
        // Given
        ClientExpediteur savedClient = repository.save(client1);

        // When
        Optional<ClientExpediteur> found = repository.findById(savedClient.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(found.get().getPhoneNumber()).isEqualTo("+1234567890");
    }

    @Test
    void shouldReturnEmptyWhenClientExpediteurNotFoundById() {
        // When
        Optional<ClientExpediteur> found = repository.findById("non-existent-id");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllClientExpediteurs() {
        // Given
        repository.save(client1);
        repository.save(client2);
        repository.save(client3);

        // When
        List<ClientExpediteur> clients = repository.findAll();

        // Then
        assertThat(clients).hasSize(3);
        assertThat(clients)
                .extracting(ClientExpediteur::getFirstName)
                .containsExactlyInAnyOrder("John", "Jane", "Bob");
    }

    @Test
    void shouldFindAllClientExpediteursWithPagination() {
        // Given
        repository.save(client1);
        repository.save(client2);
        repository.save(client3);
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<ClientExpediteur> clientPage = repository.findAll(pageable);

        // Then
        assertThat(clientPage.getContent()).hasSize(2);
        assertThat(clientPage.getTotalElements()).isEqualTo(3);
        assertThat(clientPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldUpdateClientExpediteurSuccessfully() {
        // Given
        ClientExpediteur savedClient = repository.save(client1);
        String originalId = savedClient.getId();

        // When - Update the client
        savedClient.setFirstName("Johnny");
        savedClient.setLastName("Doe Updated");
        savedClient.setEmail("johnny.doe@example.com");
        savedClient.setPhoneNumber("+9999999999");
        savedClient.setAdresse(new Adresse("Lille", "999 Updated St", "59000"));
        ClientExpediteur updated = repository.save(savedClient);

        // Then
        assertThat(updated.getId()).isEqualTo(originalId);
        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        assertThat(updated.getLastName()).isEqualTo("Doe Updated");
        assertThat(updated.getEmail()).isEqualTo("johnny.doe@example.com");
        assertThat(updated.getPhoneNumber()).isEqualTo("+9999999999");
        assertThat(updated.getAdresse().getVille()).isEqualTo("Lille");
    }

    @Test
    void shouldDeleteClientExpediteurSuccessfully() {
        // Given
        ClientExpediteur savedClient = repository.save(client1);
        String clientId = savedClient.getId();

        // When
        repository.deleteById(clientId);
        Optional<ClientExpediteur> found = repository.findById(clientId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfClientExpediteurExistsById() {
        // Given
        ClientExpediteur savedClient = repository.save(client1);

        // When
        boolean exists = repository.existsById(savedClient.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfClientExpediteurDoesNotExistById() {
        // When
        boolean exists = repository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldCountClientExpediteursCorrectly() {
        // Given
        repository.save(client1);
        repository.save(client2);

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldDeleteAllClientExpediteurs() {
        // Given
        repository.save(client1);
        repository.save(client2);

        // When
        repository.deleteAll();
        List<ClientExpediteur> allClients = repository.findAll();

        // Then
        assertThat(allClients).isEmpty();
    }

    @Test
    void shouldFindClientExpediteurByEmail() {
        // Given
        repository.save(client1);
        repository.save(client2);

        // When
        Optional<ClientExpediteur> found = repository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldReturnEmptyWhenClientExpediteurNotFoundByEmail() {
        // When
        Optional<ClientExpediteur> found = repository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldSearchClientsByLastName() {
        // Given
        repository.save(client1); // John Doe
        repository.save(client2); // Jane Smith
        repository.save(client3); // Bob Johnson
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ClientExpediteur> result = repository.searchClients("Doe", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Doe");
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldSearchClientsByFirstName() {
        // Given
        repository.save(client1); // John Doe
        repository.save(client2); // Jane Smith
        repository.save(client3); // Bob Johnson
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ClientExpediteur> result = repository.searchClients("Jane", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Jane");
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Smith");
    }

    @Test
    void shouldSearchClientsByPhoneNumber() {
        // Given
        repository.save(client1); // +1234567890
        repository.save(client2); // +0987654321
        repository.save(client3); // +1122334455
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ClientExpediteur> result = repository.searchClients("1234567890", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getPhoneNumber()).isEqualTo("+1234567890");
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldSearchClientsCaseInsensitive() {
        // Given
        repository.save(client1); // John Doe
        repository.save(client2); // Jane Smith
        Pageable pageable = PageRequest.of(0, 10);

        // When - search with different case
        Page<ClientExpediteur> result = repository.searchClients("JOHN", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
        assertThat(result.getContent().get(0).getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldSearchClientsWithPartialMatch() {
        // Given
        repository.save(client1); // John Doe
        repository.save(client2); // Jane Smith
        repository.save(client3); // Bob Johnson
        Pageable pageable = PageRequest.of(0, 10);

        // When - partial search
        Page<ClientExpediteur> result = repository.searchClients("Joh", pageable);

        // Then - should match both John and Johnson
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(ClientExpediteur::getLastName)
                .containsExactlyInAnyOrder("Doe", "Johnson");
    }

    @Test
    void shouldReturnEmptyPageWhenNoSearchResults() {
        // Given
        repository.save(client1);
        repository.save(client2);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ClientExpediteur> result = repository.searchClients("NonexistentName", pageable);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void shouldSearchClientsWithPagination() {
        // Given
        repository.save(createClientExpediteur("John", "Doe", "john1@test.com", "+1111111111", new Adresse("City1", "Street1", "11111")));
        repository.save(createClientExpediteur("John", "Smith", "john2@test.com", "+2222222222", new Adresse("City2", "Street2", "22222")));
        repository.save(createClientExpediteur("John", "Brown", "john3@test.com", "+3333333333", new Adresse("City3", "Street3", "33333")));
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<ClientExpediteur> result = repository.searchClients("John", pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldSaveClientExpediteurWithSpecialCharacters() {
        // Given
        ClientExpediteur clientWithSpecialChars = createClientExpediteur(
                "José", "Muñoz", "josé.muñoz@example.com", "+33123456789",
                new Adresse("Paris-Île-de-France", "Rue de la République", "75000")
        );

        // When
        ClientExpediteur saved = repository.save(clientWithSpecialChars);
        Optional<ClientExpediteur> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("José");
        assertThat(found.get().getLastName()).isEqualTo("Muñoz");
        assertThat(found.get().getEmail()).isEqualTo("josé.muñoz@example.com");
        assertThat(found.get().getAdresse().getVille()).isEqualTo("Paris-Île-de-France");
    }

    @Test
    void shouldHandleClientExpediteurWithMaximumAllowedFieldLengths() {
        // Given - Fields at maximum length
        String maxLengthFirstName = "A".repeat(50);
        String maxLengthLastName = "B".repeat(50);
        String maxLengthEmail = "C".repeat(100);
        String maxLengthPhone = "1".repeat(20);

        ClientExpediteur clientWithMaxFields = createClientExpediteur(
                maxLengthFirstName, maxLengthLastName, maxLengthEmail, maxLengthPhone,
                new Adresse("V".repeat(255), "R".repeat(255), "C".repeat(255))
        );

        // When
        ClientExpediteur saved = repository.save(clientWithMaxFields);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getFirstName()).hasSize(50);
        assertThat(saved.getLastName()).hasSize(50);
        assertThat(saved.getEmail()).hasSize(100);
        assertThat(saved.getPhoneNumber()).hasSize(20);
    }

    @Test
    void shouldSaveClientExpediteurWithNullEmail() {
        // Given - Email can be null according to entity definition
        ClientExpediteur clientWithNullEmail = createClientExpediteur(
                "No", "Email", null, "+1234567890",
                new Adresse("City", "Street", "12345")
        );

        // When
        ClientExpediteur saved = repository.save(clientWithNullEmail);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isNull();
        assertThat(saved.getFirstName()).isEqualTo("No");
    }

    @Test
    void shouldMaintainClientRoleAutomatically() {
        // Given
        ClientExpediteur newClient = createClientExpediteur(
                "Test", "User", "test@example.com", "+1234567890",
                new Adresse("Test City", "Test Street", "12345")
        );

        // When
        ClientExpediteur saved = repository.save(newClient);

        // Then - Role should always be CLIENT for ClientExpediteur
        assertThat(saved.getRole()).isEqualTo(PersonneRole.CLIENT);
    }

    @Test
    void shouldUpdateClientExpediteurEmailOnly() {
        // Given
        ClientExpediteur savedClient = repository.save(client1);
        String originalFirstName = savedClient.getFirstName();
        String originalLastName = savedClient.getLastName();

        // When - Update only the email
        savedClient.setEmail("updated.email@example.com");
        ClientExpediteur updated = repository.save(savedClient);

        // Then
        assertThat(updated.getEmail()).isEqualTo("updated.email@example.com");
        assertThat(updated.getFirstName()).isEqualTo(originalFirstName);
        assertThat(updated.getLastName()).isEqualTo(originalLastName);
    }

    @Test
    void shouldUpdateClientExpediteurAddressOnly() {
        // Given
        ClientExpediteur savedClient = repository.save(client1);
        String originalEmail = savedClient.getEmail();

        // When - Update only the address
        Adresse newAddress = new Adresse("Nice", "New Street", "06000");
        savedClient.setAdresse(newAddress);
        ClientExpediteur updated = repository.save(savedClient);

        // Then
        assertThat(updated.getAdresse().getVille()).isEqualTo("Nice");
        assertThat(updated.getAdresse().getRue()).isEqualTo("New Street");
        assertThat(updated.getAdresse().getCodePostal()).isEqualTo("06000");
        assertThat(updated.getEmail()).isEqualTo(originalEmail);
    }

    @Test
    void shouldFindClientExpediteurAfterMultipleUpdates() {
        // Given
        ClientExpediteur savedClient = repository.save(client1);
        String clientId = savedClient.getId();

        // When - Perform multiple updates
        savedClient.setFirstName("First Update");
        repository.save(savedClient);

        savedClient.setEmail("first.update@example.com");
        repository.save(savedClient);

        savedClient.setAdresse(new Adresse("Final City", "Final Street", "99999"));
        repository.save(savedClient);

        // Then
        Optional<ClientExpediteur> found = repository.findById(clientId);
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("First Update");
        assertThat(found.get().getEmail()).isEqualTo("first.update@example.com");
        assertThat(found.get().getAdresse().getVille()).isEqualTo("Final City");
    }

    // Helper method to create valid ClientExpediteur instances
    private ClientExpediteur createClientExpediteur(String firstName, String lastName, String email,
                                                    String phoneNumber, Adresse adresse) {
        return new ClientExpediteur(firstName, lastName, email, phoneNumber, adresse);
    }
}
package com.smartlogi.smartlogidms.masterdata.recipient.domain;

import com.smartlogi.smartlogidms.masterdata.shared.domain.Adresse;
import com.smartlogi.smartlogidms.masterdata.shared.domain.Personne.PersonneRole;
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
class RecipientRepositoryTest {

    @Autowired
    private RecipientRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Recipient recipient1;
    private Recipient recipient2;
    private Recipient recipient3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.clear();

        // Create test data
        recipient1 = createRecipient(
                "John", "Doe", "john.doe@example.com", "+1234567890",
                new Adresse("Paris", "123 Main St", "75001")
        );

        recipient2 = createRecipient(
                "Jane", "Smith", "jane.smith@example.com", "+0987654321",
                new Adresse("Lyon", "456 Oak Ave", "69001")
        );

        recipient3 = createRecipient(
                "Bob", "Johnson", "bob.johnson@example.com", "+1122334455",
                new Adresse("Marseille", "789 Pine Rd", "13001")
        );
    }

    @Test
    void shouldSaveRecipientSuccessfully() {
        // Given
        Recipient newRecipient = createRecipient(
                "Alice", "Brown", "alice.brown@example.com", "+5566778899",
                new Adresse("Toulouse", "321 Elm St", "31000")
        );

        // When
        Recipient saved = repository.save(newRecipient);

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
    void shouldFindRecipientById() {
        // Given
        Recipient savedRecipient = repository.save(recipient1);

        // When
        Optional<Recipient> found = repository.findById(savedRecipient.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(found.get().getPhoneNumber()).isEqualTo("+1234567890");
    }

    @Test
    void shouldReturnEmptyWhenRecipientNotFoundById() {
        // When
        Optional<Recipient> found = repository.findById("non-existent-id");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllRecipients() {
        // Given
        repository.save(recipient1);
        repository.save(recipient2);
        repository.save(recipient3);

        // When
        List<Recipient> recipients = repository.findAll();

        // Then
        assertThat(recipients).hasSize(3);
        assertThat(recipients)
                .extracting(Recipient::getFirstName)
                .containsExactlyInAnyOrder("John", "Jane", "Bob");
    }

    @Test
    void shouldFindAllRecipientsWithPagination() {
        // Given
        repository.save(recipient1);
        repository.save(recipient2);
        repository.save(recipient3);
        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Recipient> recipientPage = repository.findAll(pageable);

        // Then
        assertThat(recipientPage.getContent()).hasSize(2);
        assertThat(recipientPage.getTotalElements()).isEqualTo(3);
        assertThat(recipientPage.getTotalPages()).isEqualTo(2);
    }

    @Test
    void shouldUpdateRecipientSuccessfully() {
        // Given
        Recipient savedRecipient = repository.save(recipient1);
        String originalId = savedRecipient.getId();

        // When - Update the recipient
        savedRecipient.setFirstName("Johnny");
        savedRecipient.setLastName("Doe Updated");
        savedRecipient.setEmail("johnny.doe@example.com");
        savedRecipient.setPhoneNumber("+9999999999");
        savedRecipient.setAdresse(new Adresse("Lille", "999 Updated St", "59000"));
        Recipient updated = repository.save(savedRecipient);

        // Then
        assertThat(updated.getId()).isEqualTo(originalId);
        assertThat(updated.getFirstName()).isEqualTo("Johnny");
        assertThat(updated.getLastName()).isEqualTo("Doe Updated");
        assertThat(updated.getEmail()).isEqualTo("johnny.doe@example.com");
        assertThat(updated.getPhoneNumber()).isEqualTo("+9999999999");
        assertThat(updated.getAdresse().getVille()).isEqualTo("Lille");
    }

    @Test
    void shouldDeleteRecipientSuccessfully() {
        // Given
        Recipient savedRecipient = repository.save(recipient1);
        String recipientId = savedRecipient.getId();

        // When
        repository.deleteById(recipientId);
        Optional<Recipient> found = repository.findById(recipientId);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfRecipientExistsById() {
        // Given
        Recipient savedRecipient = repository.save(recipient1);

        // When
        boolean exists = repository.existsById(savedRecipient.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfRecipientDoesNotExistById() {
        // When
        boolean exists = repository.existsById("non-existent-id");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldCountRecipientsCorrectly() {
        // Given
        repository.save(recipient1);
        repository.save(recipient2);

        // When
        long count = repository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldDeleteAllRecipients() {
        // Given
        repository.save(recipient1);
        repository.save(recipient2);

        // When
        repository.deleteAll();
        List<Recipient> allRecipients = repository.findAll();

        // Then
        assertThat(allRecipients).isEmpty();
    }

    @Test
    void shouldFindRecipientByEmail() {
        // Given
        repository.save(recipient1);
        repository.save(recipient2);

        // When
        Optional<Recipient> found = repository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
    }

    @Test
    void shouldReturnEmptyWhenRecipientNotFoundByEmail() {
        // When
        Optional<Recipient> found = repository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldSaveRecipientWithSpecialCharacters() {
        // Given
        Recipient recipientWithSpecialChars = createRecipient(
                "José", "Muñoz", "josé.muñoz@example.com", "+33123456789",
                new Adresse("Paris-Île-de-France", "Rue de la République", "75000")
        );

        // When
        Recipient saved = repository.save(recipientWithSpecialChars);
        Optional<Recipient> found = repository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("José");
        assertThat(found.get().getLastName()).isEqualTo("Muñoz");
        assertThat(found.get().getEmail()).isEqualTo("josé.muñoz@example.com");
        assertThat(found.get().getAdresse().getVille()).isEqualTo("Paris-Île-de-France");
    }

    @Test
    void shouldHandleRecipientWithMaximumAllowedFieldLengths() {
        // Given - Fields at maximum length
        String maxLengthFirstName = "A".repeat(50);
        String maxLengthLastName = "B".repeat(50);
        String maxLengthEmail = "C".repeat(100);
        String maxLengthPhone = "1".repeat(20);

        Recipient recipientWithMaxFields = createRecipient(
                maxLengthFirstName, maxLengthLastName, maxLengthEmail, maxLengthPhone,
                new Adresse("V".repeat(255), "R".repeat(255), "C".repeat(255))
        );

        // When
        Recipient saved = repository.save(recipientWithMaxFields);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getFirstName()).hasSize(50);
        assertThat(saved.getLastName()).hasSize(50);
        assertThat(saved.getEmail()).hasSize(100);
        assertThat(saved.getPhoneNumber()).hasSize(20);
    }

    @Test
    void shouldSaveRecipientWithMinimumData() {
        // Given - Recipient with minimal valid data
        Recipient minimalRecipient = createRecipient(
                "A", "B", "a@b.c", "1",
                new Adresse("C", "D", "E")
        );

        // When
        Recipient saved = repository.save(minimalRecipient);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("A");
        assertThat(saved.getLastName()).isEqualTo("B");
        assertThat(saved.getEmail()).isEqualTo("a@b.c");
        assertThat(saved.getPhoneNumber()).isEqualTo("1");
    }

    @Test
    void shouldUpdateRecipientEmailOnly() {
        // Given
        Recipient savedRecipient = repository.save(recipient1);
        String originalFirstName = savedRecipient.getFirstName();
        String originalLastName = savedRecipient.getLastName();

        // When - Update only the email
        savedRecipient.setEmail("updated.email@example.com");
        Recipient updated = repository.save(savedRecipient);

        // Then
        assertThat(updated.getEmail()).isEqualTo("updated.email@example.com");
        assertThat(updated.getFirstName()).isEqualTo(originalFirstName);
        assertThat(updated.getLastName()).isEqualTo(originalLastName);
    }

    @Test
    void shouldUpdateRecipientAddressOnly() {
        // Given
        Recipient savedRecipient = repository.save(recipient1);
        String originalEmail = savedRecipient.getEmail();

        // When - Update only the address
        Adresse newAddress = new Adresse("Nice", "New Street", "06000");
        savedRecipient.setAdresse(newAddress);
        Recipient updated = repository.save(savedRecipient);

        // Then
        assertThat(updated.getAdresse().getVille()).isEqualTo("Nice");
        assertThat(updated.getAdresse().getRue()).isEqualTo("New Street");
        assertThat(updated.getAdresse().getCodePostal()).isEqualTo("06000");
        assertThat(updated.getEmail()).isEqualTo(originalEmail);
    }

    @Test
    void shouldFindRecipientAfterMultipleUpdates() {
        // Given
        Recipient savedRecipient = repository.save(recipient1);
        String recipientId = savedRecipient.getId();

        // When - Perform multiple updates
        savedRecipient.setFirstName("First Update");
        repository.save(savedRecipient);

        savedRecipient.setEmail("first.update@example.com");
        repository.save(savedRecipient);

        savedRecipient.setAdresse(new Adresse("Final City", "Final Street", "99999"));
        repository.save(savedRecipient);

        // Then
        Optional<Recipient> found = repository.findById(recipientId);
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("First Update");
        assertThat(found.get().getEmail()).isEqualTo("first.update@example.com");
        assertThat(found.get().getAdresse().getVille()).isEqualTo("Final City");
    }

    @Test
    void shouldSaveRecipientWithNullEmail() {
        // Given - Email can be null according to entity definition
        Recipient recipientWithNullEmail = createRecipient(
                "No", "Email", null, "+1234567890",
                new Adresse("City", "Street", "12345")
        );

        // When
        Recipient saved = repository.save(recipientWithNullEmail);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getEmail()).isNull();
        assertThat(saved.getFirstName()).isEqualTo("No");
    }

    @Test
    void shouldMaintainClientRoleAutomatically() {
        // Given
        Recipient newRecipient = createRecipient(
                "Test", "User", "test@example.com", "+1234567890",
                new Adresse("Test City", "Test Street", "12345")
        );

        // When
        Recipient saved = repository.save(newRecipient);

        // Then - Role should always be CLIENT for Recipient
        assertThat(saved.getRole()).isEqualTo(PersonneRole.CLIENT);
    }

    // Helper method to create valid Recipient instances
    private Recipient createRecipient(String firstName, String lastName, String email,
                                      String phoneNumber, Adresse adresse) {
        return new Recipient(firstName, lastName, email, phoneNumber, adresse);
    }
}
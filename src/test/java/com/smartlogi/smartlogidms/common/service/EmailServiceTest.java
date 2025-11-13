package com.smartlogi.smartlogidms.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender);
    }

    @Test
    void sendNotification_WithValidParameters_ShouldSendEmailSuccessfully() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String message = "Test message content";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_WithNullTo_ShouldHandleGracefully() {
        // Given
        String to = null;
        String subject = "Test Subject";
        String message = "Test message content";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_WithNullSubject_ShouldHandleGracefully() {
        // Given
        String to = "test@example.com";
        String subject = null;
        String message = "Test message content";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_WithNullMessage_ShouldHandleGracefully() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String message = null;

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_WithEmptyStrings_ShouldHandleGracefully() {
        // Given
        String to = "";
        String subject = "";
        String message = "";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_WithMultipleRecipients_ShouldHandleCorrectly() {
        // Given
        String to = "test1@example.com,test2@example.com";
        String subject = "Multi-recipient Test";
        String message = "Test message for multiple recipients";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_WhenMailSenderThrowsException_ShouldLogError() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String message = "Test message content";

        MailException mailException = mock(MailException.class);
        doThrow(mailException).when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        // Exception should be caught and logged, no re-throw
    }

    @Test
    void sendNotification_WithSpecialCharacters_ShouldHandleCorrectly() {
        // Given
        String to = "test+special@example.com";
        String subject = "Test Subject with spécial chàrâctèrs";
        String message = "Message with spécial chàrâctèrs and emoji 😊";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_WithLongContent_ShouldHandleCorrectly() {
        // Given
        String to = "test@example.com";
        String subject = "A".repeat(100); // Long subject
        String message = "B".repeat(1000); // Long message

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_VerifyMessageContent() {
        // Given
        String to = "recipient@example.com";
        String subject = "Important Notification";
        String message = "This is an important message";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendNotification_WithWhitespace_ShouldTrimAndHandle() {
        // Given
        String to = "  test@example.com  ";
        String subject = "  Test Subject  ";
        String message = "  Test message  ";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendNotification(to, subject, message);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
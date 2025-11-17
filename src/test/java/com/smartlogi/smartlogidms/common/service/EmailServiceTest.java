package com.smartlogi.smartlogidms.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.stream.Stream;

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

    @ParameterizedTest
    @MethodSource("emailDataProvider")
    void sendNotification_WithVariousInputs(String to, String subject, String message) {

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendNotification(to, subject, message);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    static Stream<Arguments> emailDataProvider() {
        return Stream.of(
                Arguments.of("test@example.com", "Subject", "Content"),
                Arguments.of("  test@example.com  ", "  Subject  ", "  Content  "),
                Arguments.of("", "", ""),
                Arguments.of(null, "No recipient", "Message"),
                Arguments.of("test@example.com", null, "No subject"),
                Arguments.of("test@example.com", "Subject", null),
                Arguments.of("test@example.com,test2@example.com", "Subject", "Content"),
                Arguments.of("test@example.com", "A".repeat(100), "B".repeat(1000)),
                Arguments.of("test+emoji@example.com", "💌 Subject", "😊 Content")
        );
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
    }
}
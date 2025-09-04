package com.fcmh.femcodersmentorhub.email;

import com.fcmh.femcodersmentorhub.emails.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    private static final String TEST_EMAIL = "mentora@test.com";
    private static final String TEST_USERNAME = "Cris Mentor";
    private static final String TEST_MENTOR_NAME = "Cris Mentor";
    private static final String TEST_MENTEE_NAME = "Ana Mentee";
    private static final String TEST_TOPIC = "Spring Boot Basics";
    private static final String TEST_DATE = "2025-09-04 10:00";
    private static final String TEST_STATUS = "ACCEPTED";
    private static final String TEST_MESSAGE = "Looking forward to mentoring you!";
    private static final String TEST_LINK = "http://meet.test/123";

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>Email Content</html>");
    }

    @Test
    @DisplayName("Should send welcome notification successfully")
    void sendWelcomeNotification_WhenValidData_SendsEmail() {

        emailService.sendWelcomeNotification(TEST_EMAIL, TEST_USERNAME);

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("welcome-notification"), any(Context.class));
    }

    @Test
    @DisplayName("Should send request notification to mentor successfully")
    void sendRequestNotificationToMentor_WhenValidData_SendsEmail() {

        emailService.sendRequestNotificationToMentor(TEST_EMAIL, TEST_MENTOR_NAME, TEST_MENTEE_NAME, TEST_TOPIC, TEST_DATE);

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("request-notification"), any(Context.class));
    }

    @Test
    @DisplayName("Should send response notification to mentee successfully")
    void sendResponseNotificationToMentee_WhenValidData_SendsEmail() {

        emailService.sendResponseNotificationToMentee(TEST_EMAIL, TEST_MENTEE_NAME, TEST_MENTOR_NAME, TEST_TOPIC, TEST_STATUS, TEST_MESSAGE, TEST_LINK);

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("response-notification"), any(Context.class));
    }

    @Test
    @DisplayName("Should log error when email sending fails")
    void sendEmail_WhenMailExceptionThrown_LogsError() {

        doThrow(new RuntimeException("Mail server not available")).when(mailSender).send(mimeMessage);

        assertThatThrownBy(() -> emailService.sendWelcomeNotification(TEST_EMAIL, TEST_USERNAME))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Mail server not available");

        verify(mailSender).send(mimeMessage);
    }
}

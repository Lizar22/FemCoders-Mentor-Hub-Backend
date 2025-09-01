package com.fcmh.femcodersmentorhub.emails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final String UTF8_ENCODING = "UTF-8";
    private static final String DASHBOARD_URL = "http://localhost:8080/dashboard";

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async("taskExecutor")
    public void sendRequestNotificationToMentor(String to, String mentorName, String menteeName, String topic, String scheduledAt) {

        EmailData emailData = EmailData.builder()
                .to(to)
                .subject("New Mentorship Request - FemCoders MentorHub")
                .templateName("request-notification")
                .build();

        emailData.addVariable("mentorName", mentorName);
        emailData.addVariable("menteeName", menteeName);
        emailData.addVariable("topic", topic);
        emailData.addVariable("scheduledAt", scheduledAt);
        emailData.addVariable("dashboardUrl", DASHBOARD_URL);

        sendEmail(emailData);
    }

    @Async("taskExecutor")
    public void sendResponseNotificationToMentee(String to, String menteeName, String mentorName, String topic, String status, String responseMessage, String meetingLink) {

        EmailData emailData = EmailData.builder()
                .to(to)
                .subject("Update on Your Mentorship Request - FemCoders MentorHub")
                .templateName("response-notification")
                .build();

        emailData.addVariable("menteeName", menteeName);
        emailData.addVariable("mentorName", mentorName);
        emailData.addVariable("topic", topic);
        emailData.addVariable("status", status);
        emailData.addVariable("responseMessage", responseMessage);
        emailData.addVariable("meetingLink", meetingLink);
        emailData.addVariable("dashboardUrl", DASHBOARD_URL);

        sendEmail(emailData);
    }

    private void sendEmail(EmailData emailData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF8_ENCODING);

            helper.setTo(emailData.getTo());
            helper.setSubject(emailData.getSubject());
            helper.setFrom("noreply@femcodersmentorhub.com");

            Context context = new Context();
            emailData.getVariables().forEach(context::setVariable);

            String htmlContent = templateEngine.process(emailData.getTemplateName(), context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Request email sent successfully to: {}", emailData.getTo());

        } catch (MailException | MessagingException exception) {
            log.error("Failed to send email to {}: {}", emailData.getTo(), exception.getMessage(), exception);
        }
    }

    @Builder
    @Getter
    private static class EmailData {
        private String to;
        private String subject;
        private String templateName;
        @Builder.Default
        private Map<String, Object> variables = new HashMap<>();

        public void addVariable(String key, Object value) {
            variables.put(key, value);
        }
    }
}
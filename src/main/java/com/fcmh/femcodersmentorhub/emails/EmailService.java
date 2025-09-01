package com.fcmh.femcodersmentorhub.emails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendRequestNotificationToMentor(String to, String mentorName, String menteeName, String topic, String scheduledAt) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("New Mentorship Request - FemCoders MentorHub");

            Context context = new Context();
            context.setVariable("mentorName", mentorName);
            context.setVariable("menteeName", menteeName);
            context.setVariable("topic", topic);
            context.setVariable("scheduledAt", scheduledAt);

            String htmlContent = templateEngine.process("templates/request-notification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Request email sent to mentor {}", to);

        } catch (MailException | MessagingException exception) {
            log.error("Error sending request email to {}: {}", to, exception.getMessage(), exception);
        }
    }

    @Async
    public void sendResponseNotificationToMentee(String to, String menteeName, String mentorName, String topic, String status, String responseMessage, String meetingLink) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Update on Your Mentorship Request - FemCoders MentorHub");

            Context context = new Context();
            context.setVariable("menteeName", menteeName);
            context.setVariable("mentorName", mentorName);
            context.setVariable("topic", topic);
            context.setVariable("status", status);
            context.setVariable("responseMessage", responseMessage);
            context.setVariable("meetingLink", meetingLink);

            String htmlContent = templateEngine.process("templates/response-notification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Response email sent to mentee {}", to);

        } catch (MailException | MessagingException exception) {
            log.error("Error sending response email to {}: {}", to, exception.getMessage(), exception);
        }
    }
}
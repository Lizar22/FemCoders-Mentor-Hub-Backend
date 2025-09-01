package com.fcmh.femcodersmentorhub.emails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Async
    public void sendRequestNotificationToMentor(String mentorEmail, String mentorName, String menteeName, String topic, String scheduledAt) {

    }
}

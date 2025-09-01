/*
package com.fcmh.femcodersmentorhub.emails;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailService emailService;

    @GetMapping("/email/mentor")
    public String testMentorEmail() {
        emailService.sendRequestNotificationToMentor(
                "mentor@test.com",
                "Ana García",
                "lucia_dev",
                "Code review: Java",
                "02/09/2025 at 15:30"
        );

        return "Email sent to mentor successfully. Check MailHog at http://localhost:8025";
    }

    @GetMapping("/email/mentee")
    public String testMenteeEmail() {
        emailService.sendResponseNotificationToMentee(
                "mentee@test.com",
                "lucia_dev",
                "Ana García",
                "Code review: Java",
                "ACCEPTED",
                "Perfect. See you tomorrow.",
                "https://meet.google.com/abc-defg-hij"
        );

        return "Email sent to mentee successfully. Check MailHog at http://localhost:8025";
    }
}*/

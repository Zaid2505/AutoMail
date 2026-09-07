package com.email.agent.controller;

import com.email.agent.config.InboxProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RequestMapping
@RestController
public class MailController {

    private final JavaMailSender mailSender;
    private final InboxProperties inbox;

    public MailController(JavaMailSender mailSender, InboxProperties inbox) {
        this.mailSender = mailSender;
        this.inbox = inbox;
    }
    @PostMapping("/seed-mail")
    public ResponseEntity<SeedResult> seed(
            @RequestParam(defaultValue = "customer@example.com") String from,
            @RequestParam(defaultValue = "Test support request") String subject,
            @RequestParam(defaultValue = "Hi, I need help with my recent order.") String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(inbox.address());
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            return ResponseEntity.status(502).body(SeedResult.failed(from, inbox.address(), subject, e));
        }

        return ResponseEntity.ok(SeedResult.sent(from, inbox.address(), subject, body));
    }
    public record SeedResult(
            String status,
            String message,
            String from,
            String to,
            String subject,
            String body,
            String error,
            Instant timestamp
    ) {
        static SeedResult sent(String from, String to, String subject, String body) {
            return new SeedResult("sent",
                    "Email delivered to %s; the agent will pick it up on the next inbox poll.".formatted(to),
                    from, to, subject, body, null, Instant.now());
        }

        static SeedResult failed(String from, String to, String subject, Exception e) {
            return new SeedResult("failed",
                    "Could not deliver email to %s. Is the Mailpit SMTP server running?".formatted(to),
                    from, to, subject, null, e.getMessage(), Instant.now());
        }
    }
}


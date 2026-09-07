package com.email.agent.service;

import com.email.agent.client.MailpitClient;
import com.email.agent.client.dto.MailpitAddress;
import com.email.agent.client.dto.MailpitMessage;
import com.email.agent.client.dto.MailpitMessageSummary;
import com.email.agent.config.InboxProperties;
import com.email.agent.dto.IncomingEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class InboxMonitor {

    private static final Logger log = LoggerFactory.getLogger(InboxMonitor.class);

    private final MailpitClient mailpit;
    private final EmailHandler handler;
    private final InboxProperties props;

    public InboxMonitor(MailpitClient mailpit, EmailHandler handler, InboxProperties props) {
        this.mailpit = mailpit;
        this.handler = handler;
        this.props = props;
    }
    @Scheduled(fixedDelayString = "${support-agent.inbox.poll-interval:10000}")
    public void poll() {
        try {
            List<MailpitMessageSummary> unread = mailpit.listUnread(props.batchSize());
            if (unread.isEmpty()) {
                log.debug("No new mail");
                return;
            }
            log.info("Found {} new message(s)", unread.size());
            for (MailpitMessageSummary summary : unread) {
                processOne(summary.id());
            }
        } catch (Exception e) {

            log.warn("Inbox poll failed: {}", e.getMessage(), e);
        }
    }
    private void processOne(String id) {
        try {
            MailpitMessage message = mailpit.getMessage(id);
            IncomingEmail email = toIncomingEmail(message);
            boolean handled = handler.handle(email);
            if (!handled) {
                mailpit.setRead(id, false);
            }
        } catch (Exception e) {
            log.error("Failed to process message {}; resetting to unread for retry", id, e);
            try {
                mailpit.setRead(id, false);
            } catch (Exception reset) {
                log.warn("Could not reset message {} to unread: {}", id, reset.getMessage());
            }
        }
    }
    private IncomingEmail toIncomingEmail(MailpitMessage message) {
        String from = message.from() != null ? message.from().address() : "(unknown)";
        List<String> to = message.to().stream()
                .map(MailpitAddress::address)
                .toList();
        String subject = message.subject() != null ? message.subject() : "";
        String body = bestBody(message);
        Instant receivedAt = parseDate(message.date());
        return new IncomingEmail(message.messageId(), from, to, subject, body, receivedAt);
    }

    private String bestBody(MailpitMessage message) {
        if (message.text() != null && !message.text().isBlank()) {
            return message.text().strip();
        }
        return message.html() != null ? message.html().strip() : "";
    }

    private Instant parseDate(String date) {
        if (date == null || date.isBlank()) {
            return Instant.now();
        }
        try {
            return OffsetDateTime.parse(date).toInstant();
        } catch (Exception e) {
            try {
                return Instant.parse(date);
            } catch (Exception ex) {
                return Instant.now();
            }
        }
    }
}

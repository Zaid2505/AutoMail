package com.email.agent.service;

import com.email.agent.dto.AgentResponse;
import com.email.agent.dto.IncomingEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class AgentEmailHandler implements EmailHandler {

    private static final Logger log = LoggerFactory.getLogger(AgentEmailHandler.class);

    private final SupportAgent agent;
    private final SupportMailSender mailSender;

    public AgentEmailHandler(SupportAgent agent, SupportMailSender mailSender) {
        this.agent = agent;
        this.mailSender = mailSender;
    }

    @Override
    public boolean handle(IncomingEmail email) {
        log.info("Handing email from {} (subject: \"{}\") to the support agent", email.from(), email.subject());
        try {
            AgentResponse response = agent.resolve(email);
            log.info("""
                    === Agent resolution ===
                    From    : {}
                    Subject : {}
                    Outcome :
                    {}
                    ========================""",
                    email.from(), email.subject(), response.operatorSummary());
            mailSender.sendReply(email, response);
            return true;
        } catch (Exception e) {

            log.error("Agent failed to resolve email from {} (subject: \"{}\"); will retry",
                    email.from(), email.subject(), e);
            return false;
        }
    }
}
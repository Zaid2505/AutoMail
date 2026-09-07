package com.email.agent.service;

import com.email.agent.dto.IncomingEmail;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;



@Component
public class LoggingEmailHandler implements EmailHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailHandler.class);

    @Override
    public boolean handle(IncomingEmail email) {
        log.info("""
                === New email ===
                From    : {}
                To      : {}
                Subject : {}
                Body    :
                {}
                =================""",
                email.from(), email.to(), email.subject(), email.body());
        return true;
    }
}

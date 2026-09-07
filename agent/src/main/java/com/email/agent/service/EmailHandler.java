package com.email.agent.service;

import com.email.agent.dto.IncomingEmail;

public interface EmailHandler {

    boolean handle(IncomingEmail email);
}
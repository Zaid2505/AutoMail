package com.email.agent.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MailpitAddress(
        @JsonProperty("Name") String name,
        @JsonProperty("Address") String address
) {
}
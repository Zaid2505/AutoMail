package com.email.agent.client;

import com.email.agent.client.dto.MailpitMessage;
import com.email.agent.client.dto.MailpitMessageSummary;
import com.email.agent.client.dto.MailpitMessagesResponse;
import com.email.agent.config.InboxProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class MailpitClient {

    private final RestClient rest;
    private final String inboxAddress;

    public MailpitClient(InboxProperties props) {
        this.rest = RestClient.builder()
                .baseUrl(props.baseUrl())
                .build();

        this.inboxAddress = props.address();
    }
    public List<MailpitMessageSummary> listUnread(int limit) {
        String query = "is:unread to:%s !from:%s".formatted(inboxAddress, inboxAddress);
        MailpitMessagesResponse response = rest.get()
                .uri(uri -> uri.path("/api/v1/search")
                        .queryParam("query", query)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(MailpitMessagesResponse.class);
        return response != null ? response.messages() : List.of();
    }

    public MailpitMessage getMessage(String id) {
        return rest.get()
                .uri("/api/v1/message/{id}", id)
                .retrieve()
                .body(MailpitMessage.class);
    }


    public void setRead(String id, boolean read) {
        rest.put()
                .uri("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ReadUpdate(List.of(id), read))
                .retrieve()
                .toBodilessEntity();
    }

    private record ReadUpdate(
            @JsonProperty("IDs") List<String> ids,
            @JsonProperty("Read") boolean read
    ) {
    }
}
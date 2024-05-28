package com.ufpa.lafocabackend.domain.service;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;
import java.util.Set;

public interface EmailService {

    void send(Message message);

    @Getter
    @Builder
    class Message {

        @Singular
        private Set<String> recipients;

        @NotNull
        private String subject;

        @NotNull
        private String body;

        @Singular("variable")
        private Map<String, Object> variables;
    }

}

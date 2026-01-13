package com.example.prueba.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GroqService {

    private final RestClient restClient;
    private final String model;

    public GroqService(
            @Value("${spring.ai.openai.api.key}") String apiKey,
            @Value("${spring.ai.openai.base.url}") String baseUrl,
            @Value("${spring.ai.openai.model}") String model
    ) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String chat(String prompt) {
        ChatRequest request = new ChatRequest(model, List.of(new Message("user", prompt)));

        ChatResponse response = restClient.post()
                .uri("/v1/chat/completions")
                .body(request)
                .retrieve()
                .body(ChatResponse.class);

        return response != null && response.choices() != null && !response.choices().isEmpty()
                ? response.choices().getFirst().message().content()
                : "No se pudo obtener respuesta del modelo";
    }

    // Records para mapeo JSON type-safe y sin warnings
    private record ChatRequest(String model, List<Message> messages) {
    }

    private record Message(String role, String content) {
    }

    private record ChatResponse(List<Choice> choices) {
    }

    private record Choice(Message message) {
    }
}
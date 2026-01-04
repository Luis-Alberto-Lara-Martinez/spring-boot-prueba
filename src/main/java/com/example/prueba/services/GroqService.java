package com.example.prueba.services;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class GroqService {

    private final OpenAiChatModel openAiChatModel;

    public GroqService(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    public String chat(String prompt) {
        // Construye una solicitud simple de chat con mensaje de usuario
        Prompt request = new Prompt(new UserMessage(prompt));

        // Llama al modelo
        ChatResponse response = openAiChatModel.call(request);

        // Extrae el contenido de la respuesta del modelo
        return response.getResult().getOutput().getText();
    }
}
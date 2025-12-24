package com.example.prueba.controller;

import com.example.prueba.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PruebaController {

    private final EmailService emailService;

    public PruebaController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/send-welcome")
    public ResponseEntity<String> sendWelcome() {
        try {
            emailService.sendWelcomeEmail("luisalbertolaramartinez3c@gmail.com", "Luis Alberto");
            return ResponseEntity.ok("Correo enviado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @Value("${resend.api.key}")
    private String apiKey;

    @GetMapping("/resend-key")
    public String test() {
        return "API Key: " + apiKey;
    }

    @GetMapping("/prueba")
    public String prueba() {
        return "¡Hola! Este es un controlador de prueba funcionando correctamente.";
    }

    @GetMapping("/saludo")
    public String saludo() {
        return "¡Bienvenido a la API de prueba!";
    }
}

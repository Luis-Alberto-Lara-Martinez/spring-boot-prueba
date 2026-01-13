package com.example.prueba.controller;

import com.example.prueba.services.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PruebaController {

    private final EmailService emailService;
    private final ResetTokenService resetTokenService;
    private final JwtService jwtService;
    private final CloudinaryService cloudinaryService;
    private final GroqService groqService;
    private final GoogleService googleService;

    public PruebaController(
            EmailService emailService,
            ResetTokenService resetTokenService,
            JwtService jwtService,
            CloudinaryService cloudinaryService,
            GroqService groqService,
            GoogleService googleService) {
        this.emailService = emailService;
        this.resetTokenService = resetTokenService;
        this.jwtService = jwtService;
        this.cloudinaryService = cloudinaryService;
        this.groqService = groqService;
        this.googleService = googleService;
    }

    @PostMapping("/public/google")
    public ResponseEntity<?> loginWithGoogle(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        try {
            GoogleIdToken.Payload payload = googleService.verifyToken(token);
            return ResponseEntity.ok(Map.of(
                    "respuesta", "Todo ok jose luis",
                    "ID de Google", payload.getSubject(),
                    "email", payload.getEmail(),
                    "name", payload.get("name"),
                    "picture", payload.get("picture"),
                    "payload completo", payload
            ));
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


    @PostMapping("upload-file")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/public/ia")
    public String iaPrueba() {
        String texto = """
                 extrae los filtros para el acceso a mi bbdd en json de este texto:\s
                 piso de 3 habitaciones 2 baños piscina trastero y garaje por menos de 400000
                \s""";
        return groqService.chat(texto);
    }

    // Endpoints en /pruebas/** - Acceso público (sin autenticación)
    @GetMapping("/public/saludo")
    public ResponseEntity<?> saludoPublico() {
        String saludo = "¡Hola! Este endpoint es público y no requiere autenticación.";
        return ResponseEntity.ok(Map.of(
                "message", saludo
        ));
    }

    @GetMapping("/public/generar-token")
    public Map<String, String> generarToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", 1);
        extraClaims.put("role", "usuario");
        String jwtToken = jwtService.generateToken("luis@gmail.com", extraClaims);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("message", "Token JWT generado correctamente. Usa este token en el header Authorization: Bearer {token}");
        return response;
    }

    // Endpoints en /auth/** - Requieren JWT
    @GetMapping("/perfil")
    public Map<String, String> perfilProtegido() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        String email = authentication.getName();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Acceso autorizado al perfil");
        response.put("email", email);
        return response;
    }

    @GetMapping("/admin/datos")
    public String datosPrivados() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        return "Estos son datos privados accesibles solo con JWT de admin. Usuario: " + authentication.getName();
    }

    @GetMapping("/public/send-welcome")
    public String sendWelcomeEmail() {
        try {
            emailService.sendWelcomeEmail("luisalbertolaramartinez3c@gmail.com", "Luis Alberto");
            return "Correo de bienvenida enviado correctamente";
        } catch (Exception e) {
            return "Error al enviar el correo de bienvenida: " + e.getMessage();
        }
    }

    @GetMapping("/api/send-reset-password")
    public String sendResetPasswordEmail() {
        try {
            String resetLink = "https://homely.example.com/reset-password?token=abc123xyz";
            emailService.sendResetPasswordEmail("luisalbertolaramartinez3c@gmail.com", "Luis Alberto", resetLink, "30 minutos");
            return "Correo de restablecimiento de contraseña enviado correctamente";
        } catch (Exception e) {
            return "Error al enviar el correo de restablecimiento: " + e.getMessage();
        }
    }

    @GetMapping("/api/prueba")
    public String prueba() {
        return "¡Hola! Este es un controlador de prueba funcionando correctamente.";
    }

    @GetMapping("/api/saludo")
    public String saludo() {
        return "¡Bienvenido a la API de prueba!";
    }

    @GetMapping("/api/token-r")
    public String tokenR() {
        String rawToken = resetTokenService.generateSecureRandomToken();
        String hashedToken = resetTokenService.hashRandomToken(rawToken);
        return "Raw Token: " + rawToken + "\nHashed Token: " + hashedToken;
    }

    @GetMapping("/api/jwt")
    public String jwt() {
        try {
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("id", 1);
            extraClaims.put("role", "admin");
            String jwtToken = jwtService.generateToken("luis@gmail.com", extraClaims);
            return "Generated JWT: " + jwtToken;
        } catch (Exception e) {
            return "Error al generar JWT: " + e.getMessage();
        }
    }
}
package com.example.prueba;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendWelcomeEmail(String to, String name) throws Exception {
        // Cargar y procesar la plantilla HTML desde resources/templates/bienvenida.html
        ClassPathResource resource = new ClassPathResource("templates/bienvenida.html");
        String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        html = html.replace("${name}", name);

        SendEmailRequest params = SendEmailRequest.builder()
                .from("Resend <onboarding@resend.dev>")
                .to(to)
                .subject("¡Bienvenido a Homely, %s!".formatted(name))
                .html(html)
                .build();

        resend.emails().send(params);
    }
}

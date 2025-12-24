package com.example.prueba;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendWelcomeEmail(String to, String name) throws Exception {

        String html = """
                <html>
                    <body style="font-family: Arial, sans-serif;">
                        <h2>¡Bienvenido, %s!</h2>
                        <p>Gracias por unirte a nuestra plataforma.</p>
                        <p>Estamos encantados de tenerte aquí.</p>
                        <br>
                        <p>— El equipo de Homely</p>
                    </body>
                </html>
                """.formatted(name);

        SendEmailRequest params = SendEmailRequest.builder()
                .from("Resend <onboarding@resend.dev>")
                .to(to)
                .subject("¡Bienvenido a Homely, %s!".formatted(name))
                .html(html)
                .build();

        resend.emails().send(params);
    }
}

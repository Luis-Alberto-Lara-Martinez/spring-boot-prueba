package com.example.prueba.services;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.Map;

@Service
public class EmailService {

    private static final String FROM_EMAIL = "Homely <comunications@homelyweb.app>";
    private final Resend resend;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendWelcomeEmail(String to, String name) throws Exception {
        Map<String, Object> variablesHtml = Map.of(
                "name", name,
                "year", Year.now().getValue()
        );

        sendEmail(to, "¡Bienvenido a Homely, %s!".formatted(name), "templates/welcome.html", variablesHtml);
    }

    public void sendResetPasswordEmail(String to, String name, String resetLink, String expirationTime) throws Exception {
        Map<String, Object> variables = Map.of(
                "name", name,
                "resetLink", resetLink,
                "expirationTime", expirationTime,
                "year", Year.now().getValue());

        sendEmail(to, "Restablece tu contraseña de Homely", "templates/reset-password.html", variables);
    }

    private void sendEmail(String to, String subject, String templatePath, Map<String, Object> variables) throws Exception {
        String htmlTemplate = loadTemplateAndReplace(templatePath, variables);

        SendEmailRequest params = SendEmailRequest.builder()
                .from(FROM_EMAIL)
                .to(to)
                .subject(subject)
                .html(htmlTemplate)
                .text(htmlToPlainText(htmlTemplate))
                .build();

        resend.emails().send(params);
    }

    private String loadTemplateAndReplace(String templatePath, Map<String, Object> variables) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            html = html.replace("${" + entry.getKey() + "}", value);
        }

        return html;
    }

    private String htmlToPlainText(String html) {
        return Jsoup.parse(html).text();
    }
}

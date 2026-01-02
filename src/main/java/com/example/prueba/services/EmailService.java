package com.example.prueba.services;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {

    private final Resend resend;
    private final String apiKey;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
        this.apiKey = apiKey;
    }

    public String mostrarApiKey() {
        return this.apiKey;
    }

    public void sendWelcomeEmail(String to, String name) throws Exception {
        String plantillaHtml = loadTemplateAndReplace("templates/welcome.html", Map.of(
                "name", name,
                "year", String.valueOf(java.time.Year.now().getValue())
        ));

        SendEmailRequest params = SendEmailRequest.builder()
                .from("Homely <no-reply@homelyweb.app>")
                .to(to)
                .subject("¡Bienvenido a Homely, %s!".formatted(name))
                .html(plantillaHtml)
                .text(htmlToPlainText(plantillaHtml))
                .build();

        resend.emails().send(params);
    }

    public void sendResetPasswordEmail(String to, String name, String resetLink, String expirationTime) throws Exception {
        String plantillaHtml = loadTemplateAndReplace("templates/reset-password.html", Map.of(
                "name", name,
                "resetLink", resetLink,
                "expirationTime", expirationTime,
                "year", String.valueOf(java.time.Year.now().getValue())
        ));

        SendEmailRequest params = SendEmailRequest.builder()
                .from("Homely <no-reply@comunications.homelyweb.app>")
                .to(to)
                .subject("Restablece tu contraseña de Homely")
                .html(plantillaHtml)
                // .text(htmlToPlainText(plantillaHtml))
                .build();

        resend.emails().send(params);
    }

    /**
     * Método utilitario para cargar plantillas HTML y reemplazar variables usando un Map.
     * Esto hace el código más limpio y escalable.
     *
     * @param templatePath Ruta de la plantilla en resources
     * @param variables    Map con pares clave-valor donde la clave es el nombre de la variable (sin ${})
     * @return El HTML procesado con todas las variables reemplazadas
     */
    private String loadTemplateAndReplace(String templatePath, Map<String, String> variables) throws Exception {
        ClassPathResource resource = new ClassPathResource(templatePath);
        String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        // Reemplazar todas las variables del Map
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            html = html.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        return html;
    }

    /**
     * Convierte HTML a texto plano eliminando las etiquetas HTML.
     * Esto es útil para el campo 'text' del email, que es una versión alternativa
     * para clientes de correo que no soportan HTML.
     *
     * @param html El contenido HTML
     * @return El texto plano sin etiquetas HTML
     */
    private String htmlToPlainText(String html) {
        return html
                // Reemplazar <br> y <p> por saltos de línea
                .replaceAll("<br\\s*/?>", "\n")
                .replaceAll("</p>", "\n\n")
                // Eliminar todas las etiquetas HTML
                .replaceAll("<[^>]+>", "")
                // Decodificar entidades HTML comunes
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                // Limpiar espacios múltiples y saltos de línea excesivos
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}

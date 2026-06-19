package com.zentry.backend.features.correo.service;

import com.zentry.backend.core.exceptions.SolicitudInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CorreoService {

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.sender.email:ld.martinez.117@gmail.com}")
    private String brevoSenderEmail;

    private final RestTemplate restTemplate;

    public CorreoService() {
        this.restTemplate = new RestTemplate();
    }

    public void enviarCorreoSimple(String destinatario, String asunto, String mensaje) {
        enviarConBrevo(destinatario, asunto, mensaje, false);
    }

    public void enviarCorreoHtml(String destinatario, String asunto, String contenidoHtml) {
        enviarConBrevo(destinatario, asunto, contenidoHtml, true);
    }

    private void enviarConBrevo(String destinatario, String asunto, String contenido, boolean esHtml) {
        if (brevoApiKey == null || brevoApiKey.trim().isEmpty()) {
            throw new SolicitudInvalidaException("No se ha configurado la API Key de Brevo (brevo.api.key)");
        }

        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey.trim());

            Map<String, Object> body = new HashMap<>();
            
            // Remitente verificado en Brevo
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "Zentry Eventos");
            sender.put("email", brevoSenderEmail.trim());
            body.put("sender", sender);

            // Destinatario
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", destinatario.trim());
            body.put("to", Collections.singletonList(recipient));

            body.put("subject", asunto);

            if (esHtml) {
                body.put("htmlContent", contenido);
            } else {
                body.put("textContent", contenido);
            }

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CREATED) {
                throw new Exception("Código de estado devuelto por Brevo no exitoso: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new SolicitudInvalidaException("Fallo al enviar correo mediante Brevo API: " + e.getMessage());
        }
    }
}
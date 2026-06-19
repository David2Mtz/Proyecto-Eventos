package com.zentry.backend.features.correo.service;

import com.zentry.backend.core.exceptions.SolicitudInvalidaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String correoRemitente;

    public CorreoService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void enviarCorreoSimple(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage correo = new SimpleMailMessage();

            correo.setFrom(correoRemitente);
            correo.setTo(destinatario);
            correo.setSubject(asunto);
            correo.setText(mensaje);

            javaMailSender.send(correo);
        } catch (MailException e) {
            throw new SolicitudInvalidaException("No se pudo enviar el correo: " + e.getMessage());
        }
    }

    public void enviarCorreoHtml(String destinatario, String asunto, String contenidoHtml) {
        try {
            jakarta.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(correoRemitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new SolicitudInvalidaException("No se pudo enviar el correo HTML: " + e.getMessage());
        }
    }
}
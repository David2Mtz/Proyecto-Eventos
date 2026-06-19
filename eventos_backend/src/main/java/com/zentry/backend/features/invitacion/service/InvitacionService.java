package com.zentry.backend.features.invitacion.service;

import com.zentry.backend.core.exceptions.SolicitudInvalidaException;
import com.zentry.backend.features.invitacion.dto.InvitacionRequest;
import com.zentry.backend.core.domain.EstadoInvitacion;
import com.zentry.backend.core.domain.Evento;
import com.zentry.backend.core.domain.Invitacion;
import com.zentry.backend.core.domain.Invitado;
import com.zentry.backend.core.exceptions.RecursoNoEncontradoException;
import com.zentry.backend.features.evento.repository.EventoRepository;
import com.zentry.backend.features.invitacion.repository.InvitacionRepository;
import com.zentry.backend.features.invitado.repository.InvitadoRepository;
import com.zentry.backend.features.correo.service.CorreoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InvitacionService {

    private final InvitacionRepository invitacionRepository;
    private final EventoRepository eventoRepository;
    private final InvitadoRepository invitadoRepository;
    private final CorreoService correoService;

    public InvitacionService(
            InvitacionRepository invitacionRepository,
            EventoRepository eventoRepository,
            InvitadoRepository invitadoRepository,
            CorreoService correoService
    ) {
        this.invitacionRepository = invitacionRepository;
        this.eventoRepository = eventoRepository;
        this.invitadoRepository = invitadoRepository;
        this.correoService = correoService;
    }

    public List<Invitacion> listarTodas() {
        return invitacionRepository.findAll();
    }

    public List<Invitacion> listarPorEvento(Long idEvento) {
        return invitacionRepository.findInvitacionsByIdEvento(idEvento);
    }

    public Invitacion buscarPorId(Long id) {
        return invitacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Invitación no encontrada con ID: " + id));
    }

    public Invitacion buscarPorQrToken(String qrToken) {
        return invitacionRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new RecursoNoEncontradoException("Invitación no encontrada con token QR: " + qrToken));
    }

    @Transactional
    public Invitacion crearInvitacion(InvitacionRequest request) {
        var eventoSearch = eventoRepository.findById(request.getIdEvento());
       if(eventoSearch.isEmpty()) {
           throw new RecursoNoEncontradoException("Evento no encontrado");
        }
       if(invitadoRepository.existsByCorreo(request.getCorreoInvitado())){
           throw new SolicitudInvalidaException("El correo ya existe en el sistema");
       }
       var invitado = invitadoRepository.save(Invitado.builder()
                .nombre(request.getNombreInvitado())
                .correo(request.getCorreoInvitado())
                .build());
       var token = UUID.randomUUID().toString();
      var invitacion =  invitacionRepository.save(Invitacion.builder()
                        .idEvento(request.getIdEvento())
                        .idInvitado(invitado.getIdInvitado())
                        .qrToken(token)
                        .estado(EstadoInvitacion.PENDIENTE)
                .build());
        enviarCorreoInvitacion(request.getIdEvento(), request.getCorreoInvitado(),token,eventoSearch.get().getFecha(),
                eventoSearch.get().getLugar(),request.getNombreInvitado(), eventoSearch.get().getNombreEvento());
        invitacion.setInvitado(invitado);
        invitacion.setEvento(eventoSearch.get());
        return invitacion;
    }

    @Transactional
    public void eliminarInvitacion(Long id) {
        var invitacion = invitacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Invitación no encontrada con ID: " + id));
        invitacionRepository.delete(invitacion);
        if (invitacion.getIdInvitado() != null) {
            invitadoRepository.deleteById(invitacion.getIdInvitado());
        }
    }

    private void enviarCorreoInvitacion(Long idEvento, String correoInvitado, String token, LocalDateTime fecha, String lugar,String nombre, String nombreEvento) {
        String asunto = "Tu invitación para el evento: " + nombreEvento;
        String fechaFormateada = fecha != null ? fecha.toString().replace("T", " ") : "N/A";

        String contenidoHtml = String.format(
            "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; color: #1e293b;\">" +
            "  <h2 style=\"color: #6366f1; text-align: center;\">¡Tu Invitación al Evento!</h2>" +
            "  <p>Hola <strong>%s</strong>,</p>" +
            "  <p>Has sido invitado al evento <strong>%s</strong> que se llevará a cabo en:</p>" +
            "  <div style=\"background-color: #f8fafc; border: 1px solid #cbd5e1; border-radius: 6px; padding: 15px; margin: 15px 0;\">" +
            "    <p style=\"margin: 5px 0;\">📍 <strong>Lugar:</strong> %s</p>" +
            "    <p style=\"margin: 5px 0;\">📅 <strong>Fecha y Hora:</strong> %s</p>" +
            "  </div>" +
            "  <p style=\"text-align: center; margin-top: 25px;\"><strong>Tu código QR de acceso para el ingreso:</strong></p>" +
            "  <div style=\"text-align: center; margin: 20px 0;\">" +
            "    <img src=\"https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=%s\" alt=\"Código QR de Acceso\" style=\"border: 1px solid #cbd5e1; border-radius: 8px; padding: 10px;\" />" +
            "  </div>" +
            "  <p style=\"text-align: center; font-size: 0.85em; color: #64748b;\">Token de acceso: %s</p>" +
            "  <p style=\"text-align: center; font-weight: bold; margin-top: 25px; color: #6366f1;\">¡Te esperamos!</p>" +
            "</div>",
            nombre, nombreEvento, lugar, fechaFormateada, token, token
        );

        correoService.enviarCorreoHtml(correoInvitado, asunto, contenidoHtml);
    }
}

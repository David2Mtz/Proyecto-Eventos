package com.zentry.backend.features.invitacion.service;

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
        return invitacionRepository.findByEventoIdEvento(idEvento);
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
        Evento evento = eventoRepository.findById(request.getIdEvento())
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + request.getIdEvento()));

        Invitado invitado = invitadoRepository.findByCorreo(request.getCorreoInvitado())
                .orElseGet(() -> {
                    Invitado nuevo = new Invitado();
                    nuevo.setNombre(request.getNombreInvitado());
                    nuevo.setCorreo(request.getCorreoInvitado());
                    return invitadoRepository.save(nuevo);
                });

        Invitacion invitacion = new Invitacion();
        invitacion.setEvento(evento);
        invitacion.setInvitado(invitado);
        invitacion.setQrToken(UUID.randomUUID().toString());
        invitacion.setEstado(EstadoInvitacion.PENDIENTE);

        Invitacion guardada = invitacionRepository.save(invitacion);

        enviarCorreoInvitacion(guardada);

        return guardada;
    }

    private void enviarCorreoInvitacion(Invitacion invitacion) {
        String asunto = "Tu invitación para el evento: " + invitacion.getEvento().getNombreEvento();
        String mensaje = String.format(
                "Hola %s,\n\nHas sido invitado al evento '%s' que se llevará a cabo en '%s' el %s.\n\nTu código de acceso es: %s\n\n¡Te esperamos!",
                invitacion.getInvitado().getNombre(),
                invitacion.getEvento().getNombreEvento(),
                invitacion.getEvento().getLugar(),
                invitacion.getEvento().getFecha(),
                invitacion.getQrToken()
        );

        correoService.enviarCorreoSimple(invitacion.getInvitado().getCorreo(), asunto, mensaje);
    }
}

package com.zentry.backend.features.acceso.service;

import com.zentry.backend.features.acceso.dto.AccesoRequest;
import com.zentry.backend.core.domain.Acceso;
import com.zentry.backend.core.domain.EstadoInvitacion;
import com.zentry.backend.core.domain.Invitacion;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.core.exceptions.RecursoNoEncontradoException;
import com.zentry.backend.core.exceptions.SolicitudInvalidaException;
import com.zentry.backend.features.acceso.repository.AccesoRepository;
import com.zentry.backend.features.invitacion.repository.InvitacionRepository;
import com.zentry.backend.features.usuario.repository.UsuarioRepository;
import com.zentry.backend.features.correo.service.CorreoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccesoService {

    private final AccesoRepository accesoRepository;
    private final InvitacionRepository invitacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CorreoService correoService;

    public AccesoService(
            AccesoRepository accesoRepository,
            InvitacionRepository invitacionRepository,
            UsuarioRepository usuarioRepository,
            CorreoService correoService
    ) {
        this.accesoRepository = accesoRepository;
        this.invitacionRepository = invitacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.correoService = correoService;
    }

    public List<Acceso> listarTodos() {
        return accesoRepository.findAll();
    }

    public List<Acceso> listarPorEvento(Long idEvento) {
        return accesoRepository.findByInvitacionEventoIdEvento(idEvento);
    }

    @Transactional
    public Acceso registrarAcceso(AccesoRequest request) {
        Invitacion invitacion = invitacionRepository.findByQrToken(request.getQrToken())
                .orElseThrow(() -> new RecursoNoEncontradoException("Invitación no encontrada con token QR: " + request.getQrToken()));

        if (invitacion.getEstado() == EstadoInvitacion.UTILIZADO) {
            throw new SolicitudInvalidaException("Esta invitación ya ha sido utilizada");
        }

        Usuario staff = usuarioRepository.findById(request.getIdStaff())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario de Staff no encontrado con ID: " + request.getIdStaff()));

        invitacion.setEstado(EstadoInvitacion.UTILIZADO);
        invitacionRepository.save(invitacion);

        Acceso acceso = new Acceso();
        acceso.setInvitacion(invitacion);
        acceso.setStaff(staff);
        acceso.setFechaHoraEntrada(LocalDateTime.now());

        Acceso guardado = accesoRepository.save(acceso);

        enviarCorreoAccesoUtilizado(invitacion);

        return guardado;
    }

    private void enviarCorreoAccesoUtilizado(Invitacion invitacion) {
        String asunto = "¡Bienvenido! Tu código de acceso ha sido validado";
        String mensaje = String.format(
                "Hola %s,\n\nTu invitación para el evento '%s' ha sido utilizada correctamente hoy %s.\n\n¡Disfruta del evento!",
                invitacion.getInvitado().getNombre(),
                invitacion.getEvento().getNombreEvento(),
                LocalDateTime.now()
        );

        try {
            correoService.enviarCorreoSimple(invitacion.getInvitado().getCorreo(), asunto, mensaje);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Error al enviar correo de acceso: " + e.getMessage());
        }
    }
}

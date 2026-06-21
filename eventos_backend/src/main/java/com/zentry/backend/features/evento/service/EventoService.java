package com.zentry.backend.features.evento.service;

import com.zentry.backend.features.evento.dto.EventoRequest;
import com.zentry.backend.core.domain.Evento;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.core.domain.Invitacion;
import com.zentry.backend.core.domain.Acceso;
import com.zentry.backend.core.exceptions.RecursoNoEncontradoException;
import com.zentry.backend.features.evento.repository.EventoRepository;
import com.zentry.backend.features.usuario.repository.UsuarioRepository;
import com.zentry.backend.features.invitacion.repository.InvitacionRepository;
import com.zentry.backend.features.acceso.repository.AccesoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InvitacionRepository invitacionRepository;
    private final AccesoRepository accesoRepository;

    public EventoService(EventoRepository eventoRepository, 
                         UsuarioRepository usuarioRepository, 
                         InvitacionRepository invitacionRepository, 
                         AccesoRepository accesoRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.invitacionRepository = invitacionRepository;
        this.accesoRepository = accesoRepository;
    }

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public Evento buscarPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + id));
    }

    public List<Evento> listarPorAnfitrion(Long idAnfitrion) {
        return eventoRepository.findByAnfitrionIdUsuario(idAnfitrion);
    }

    public Evento crearConImagen(EventoRequest request, String imagenUrl) {
        Usuario anfitrion = usuarioRepository.findById(request.getIdAnfitrion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Anfitrión no encontrado con ID: " + request.getIdAnfitrion()));

        Evento evento = new Evento();
        evento.setNombreEvento(request.getNombreEvento());
        evento.setFecha(request.getFecha());
        evento.setLugar(request.getLugar());
        evento.setAnfitrion(anfitrion);
        evento.setImagenUrl(imagenUrl);

        return eventoRepository.save(evento);
    }

    public Evento actualizarConImagen(Long id, EventoRequest request, String imagenUrl) {
        Evento evento = buscarPorId(id);
        Usuario anfitrion = usuarioRepository.findById(request.getIdAnfitrion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Anfitrión no encontrado con ID: " + request.getIdAnfitrion()));

        evento.setNombreEvento(request.getNombreEvento());
        evento.setFecha(request.getFecha());
        evento.setLugar(request.getLugar());
        evento.setAnfitrion(anfitrion);
        if (imagenUrl != null) {
            evento.setImagenUrl(imagenUrl);
        }

        return eventoRepository.save(evento);
    }

    @Transactional
    public void eliminar(Long id) {
        // 1. Obtener todas las invitaciones asociadas al evento
        List<Invitacion> invitaciones = invitacionRepository.findInvitacionsByIdEvento(id);

        // 2. Borrar todos los registros de acceso (check-ins) asociados a esas invitaciones
        for (Invitacion inv : invitaciones) {
            List<Acceso> accesos = accesoRepository.findAllByIdInvitacion(inv.getIdInvitacion());
            accesoRepository.deleteAll(accesos);
        }

        // 3. Borrar las invitaciones
        invitacionRepository.deleteAll(invitaciones);

        // 4. Borrar el evento
        Evento evento = buscarPorId(id);
        eventoRepository.delete(evento);
    }
}

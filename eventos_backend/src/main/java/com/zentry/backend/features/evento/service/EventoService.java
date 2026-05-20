package com.zentry.backend.features.evento.service;

import com.zentry.backend.features.evento.dto.EventoRequest;
import com.zentry.backend.core.domain.Evento;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.core.exceptions.RecursoNoEncontradoException;
import com.zentry.backend.features.evento.repository.EventoRepository;
import com.zentry.backend.features.usuario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public EventoService(EventoRepository eventoRepository, UsuarioRepository usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
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

    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }
}

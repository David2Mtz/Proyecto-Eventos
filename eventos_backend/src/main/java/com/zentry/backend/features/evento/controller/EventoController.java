package com.zentry.backend.features.evento.controller;

import com.zentry.backend.features.evento.dto.EventoRequest;
import com.zentry.backend.core.domain.Evento;
import com.zentry.backend.features.evento.service.EventoService;
import com.zentry.backend.features.storage.service.FileStorageService;
import com.zentry.backend.core.exceptions.RecursoNoEncontradoException;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.features.usuario.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final FileStorageService fileStorageService;
    private final UsuarioRepository usuarioRepository;

    public EventoController(EventoService eventoService, FileStorageService fileStorageService, UsuarioRepository usuarioRepository) {
        this.eventoService = eventoService;
        this.fileStorageService = fileStorageService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Evento> listarTodos() {
        return eventoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Evento buscarPorId(@PathVariable Long id) {
        return eventoService.buscarPorId(id);
    }

    @GetMapping("/anfitrion/{idAnfitrion}")
    public List<Evento> listarPorAnfitrion(@PathVariable Long idAnfitrion) {
        return eventoService.listarPorAnfitrion(idAnfitrion);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public Evento crear(
            @RequestParam("nombreEvento") String nombreEvento,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @RequestParam("lugar") String lugar,
            @RequestParam("idAnfitrion") Long idAnfitrion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            imagenUrl = fileStorageService.storeFile(imagen);
        }

        EventoRequest request = new EventoRequest();
        request.setNombreEvento(nombreEvento);
        request.setFecha(fecha);
        request.setLugar(lugar);
        request.setIdAnfitrion(idAnfitrion);

        return eventoService.crearConImagen(request, imagenUrl);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public Evento actualizar(
            @PathVariable Long id,
            @RequestParam("nombreEvento") String nombreEvento,
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @RequestParam("lugar") String lugar,
            @RequestParam("idAnfitrion") Long idAnfitrion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen
    ) {
        String imagenUrl = null;
        if (imagen != null && !imagen.isEmpty()) {
            imagenUrl = fileStorageService.storeFile(imagen);
        }

        EventoRequest request = new EventoRequest();
        request.setNombreEvento(nombreEvento);
        request.setFecha(fecha);
        request.setLugar(lugar);
        request.setIdAnfitrion(idAnfitrion);

        return eventoService.actualizarConImagen(id, request, imagenUrl);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        eventoService.eliminar(id);
    }
}

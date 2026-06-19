package com.zentry.backend.features.invitacion.controller;

import com.zentry.backend.features.invitacion.dto.InvitacionRequest;
import com.zentry.backend.core.domain.Invitacion;
import com.zentry.backend.features.invitacion.service.InvitacionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitaciones")
public class InvitacionController {

    private final InvitacionService invitacionService;

    public InvitacionController(InvitacionService invitacionService) {
        this.invitacionService = invitacionService;
    }

    @GetMapping
    public List<Invitacion> listarTodas() {
        return invitacionService.listarTodas();
    }

    @GetMapping("/evento/{idEvento}")
    public List<Invitacion> listarPorEvento(@PathVariable Long idEvento) {
        return invitacionService.listarPorEvento(idEvento);
    }

    @GetMapping("/{id}")
    public Invitacion buscarPorId(@PathVariable Long id) {
        return invitacionService.buscarPorId(id);
    }

    @GetMapping("/token/{qrToken}")
    public Invitacion buscarPorQrToken(@PathVariable String qrToken) {
        return invitacionService.buscarPorQrToken(qrToken);
    }

    @PostMapping
    public Invitacion crear(@Valid @RequestBody InvitacionRequest request) {
        return invitacionService.crearInvitacion(request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        invitacionService.eliminarInvitacion(id);
    }
}

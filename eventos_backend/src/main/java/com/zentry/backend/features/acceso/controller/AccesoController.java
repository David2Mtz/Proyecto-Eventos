package com.zentry.backend.features.acceso.controller;

import com.zentry.backend.features.acceso.dto.AccesoRequest;
import com.zentry.backend.core.domain.Acceso;
import com.zentry.backend.features.acceso.service.AccesoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accesos")
public class AccesoController {

    private final AccesoService accesoService;

    public AccesoController(AccesoService accesoService) {
        this.accesoService = accesoService;
    }

    @GetMapping
    public List<Acceso> listarTodos() {
        return accesoService.listarTodos();
    }

    @GetMapping("/evento/{idEvento}")
    public List<Acceso> listarPorEvento(@PathVariable Long idEvento) {
        return accesoService.listarPorEvento(idEvento);
    }

    @PostMapping("/validar")
    public Acceso registrarAcceso(@Valid @RequestBody AccesoRequest request) {
        return accesoService.registrarAcceso(request);
    }
}

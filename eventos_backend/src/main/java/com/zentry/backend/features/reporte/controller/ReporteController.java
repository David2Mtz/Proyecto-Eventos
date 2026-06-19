package com.zentry.backend.features.reporte.controller;

import com.zentry.backend.features.reporte.dto.ReporteAsistenciaDTO;
import com.zentry.backend.features.reporte.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/asistencia/evento/{idEvento}")
    public ResponseEntity<List<ReporteAsistenciaDTO>> obtenerAsistentes(@PathVariable Long idEvento) {
        List<ReporteAsistenciaDTO> asistentes = reporteService.obtenerAsistenciaEvento(idEvento);
        return ResponseEntity.ok(asistentes);
    }
}

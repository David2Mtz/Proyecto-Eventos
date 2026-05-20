package com.zentry.backend.features.reporte.controller;

import com.zentry.backend.features.reporte.service.ReporteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/asistencia/evento/{idEvento}/pdf")
    public ResponseEntity<byte[]> generarReporteAsistencia(@PathVariable Long idEvento) {
        byte[] pdf = reporteService.generarReporteAsistenciaPdf(idEvento);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-asistencia-evento-" + idEvento + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

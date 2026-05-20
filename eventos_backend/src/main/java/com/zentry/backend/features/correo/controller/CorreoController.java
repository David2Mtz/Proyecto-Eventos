package com.zentry.backend.features.correo.controller;

import com.zentry.backend.features.correo.service.CorreoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/correos")
public class CorreoController {

    private final CorreoService correoService;

    public CorreoController(CorreoService correoService) {
        this.correoService = correoService;
    }

    @PostMapping("/prueba")
    public String enviarCorreoPrueba(@RequestParam String destinatario) {
        correoService.enviarCorreoSimple(
                destinatario,
                "Correo de prueba - Zentry",
                "Hola, este es un correo de prueba enviado desde el backend de Zentry."
        );

        return "Correo enviado correctamente a " + destinatario;
    }
}

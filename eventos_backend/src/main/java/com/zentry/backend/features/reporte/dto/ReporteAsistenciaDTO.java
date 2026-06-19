package com.zentry.backend.features.reporte.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteAsistenciaDTO {
    private String nombre;
    private String correo;
    private LocalDateTime fechaHoraEntrada;
}

package com.zentry.backend.core.domain;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rol")
public class Rol implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre_rol", length = 50, nullable = false)
    private String nombreRol;

    @Column(name = "descripcion_rol", nullable = false, length = 150)
    private String descripcionRol;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    public Rol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
}

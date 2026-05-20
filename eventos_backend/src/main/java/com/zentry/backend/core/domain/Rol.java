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
@Table(name = "Rol")
public class Rol implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idRol;

    @Column(length = 50, nullable = false)
    private String nombreRol;

    @Column(nullable = false, length = 150)
    private String descripcionRol;

    @Column(nullable = false)
    private LocalDateTime fecha;
}

package com.zentry.backend.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "acceso")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class Acceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso")
    private Long idAcceso;

    @NotNull(message = "La invitación es obligatoria")
    @Column(name = "id_invitacion", nullable = false)
    private Long idInvitacion;

    @NotNull(message = "El staff es obligatorio")
    @Column(name = "id_staff", nullable = false)
    private Long idStaff;

    @Column(name = "fecha_hora_entrada", nullable = false)
    private LocalDateTime fechaHoraEntrada = LocalDateTime.now();
}

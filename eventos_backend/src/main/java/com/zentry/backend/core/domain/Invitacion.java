package com.zentry.backend.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "invitacion")
public class Invitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invitacion")
    private Long idInvitacion;

    @NotNull(message = "El evento es obligatorio")
    @Column(name = "id_evento")
    private Long idEvento;

    @NotNull(message = "El invitado es obligatorio")
    @Column(name = "id_invitado", nullable = false)
    private Long idInvitado;

    @NotBlank(message = "El token QR es obligatorio")
    @Column(name = "qr_token", nullable = false, unique = true, length = 255)
    private String qrToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInvitacion estado = EstadoInvitacion.PENDIENTE;

    @ManyToOne
    @JoinColumn(name = "id_evento", insertable = false, updatable = false)
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "id_invitado", insertable = false, updatable = false)
    private Invitado invitado;

}

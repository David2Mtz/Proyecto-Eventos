package com.zentry.backend.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "invitacion")
public class Invitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invitacion")
    private Long idInvitacion;

    @NotNull(message = "El evento es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @NotNull(message = "El invitado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_invitado", nullable = false)
    private Invitado invitado;

    @NotBlank(message = "El token QR es obligatorio")
    @Column(name = "qr_token", nullable = false, unique = true, length = 255)
    private String qrToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoInvitacion estado = EstadoInvitacion.PENDIENTE;

    public Invitacion() {
    }

    public Invitacion(Long idInvitacion, Evento evento, Invitado invitado, String qrToken, EstadoInvitacion estado) {
        this.idInvitacion = idInvitacion;
        this.evento = evento;
        this.invitado = invitado;
        this.qrToken = qrToken;
        this.estado = estado;
    }

    public Long getIdInvitacion() {
        return idInvitacion;
    }

    public void setIdInvitacion(Long idInvitacion) {
        this.idInvitacion = idInvitacion;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Invitado getInvitado() {
        return invitado;
    }

    public void setInvitado(Invitado invitado) {
        this.invitado = invitado;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public EstadoInvitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoInvitacion estado) {
        this.estado = estado;
    }
}

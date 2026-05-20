package com.zentry.backend.core.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "acceso")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso")
    private Long idAcceso;

    @NotNull(message = "La invitación es obligatoria")
    @OneToOne
    @JoinColumn(name = "id_invitacion", nullable = false)
    private Invitacion invitacion;

    @NotNull(message = "El staff es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_staff", nullable = false)
    private Usuario staff;

    @Column(name = "fecha_hora_entrada", nullable = false)
    private LocalDateTime fechaHoraEntrada = LocalDateTime.now();

    public Acceso() {
    }

    public Acceso(Long idAcceso, Invitacion invitacion, Usuario staff, LocalDateTime fechaHoraEntrada) {
        this.idAcceso = idAcceso;
        this.invitacion = invitacion;
        this.staff = staff;
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    public Long getIdAcceso() {
        return idAcceso;
    }

    public void setIdAcceso(Long idAcceso) {
        this.idAcceso = idAcceso;
    }

    public Invitacion getInvitacion() {
        return invitacion;
    }

    public void setInvitacion(Invitacion invitacion) {
        this.invitacion = invitacion;
    }

    public Usuario getStaff() {
        return staff;
    }

    public void setStaff(Usuario staff) {
        this.staff = staff;
    }

    public LocalDateTime getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) {
        this.fechaHoraEntrada = fechaHoraEntrada;
    }
}

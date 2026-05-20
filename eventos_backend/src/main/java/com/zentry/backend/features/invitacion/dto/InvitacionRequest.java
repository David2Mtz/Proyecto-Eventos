package com.zentry.backend.features.invitacion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InvitacionRequest {

    @NotNull(message = "El ID del evento es obligatorio")
    private Long idEvento;

    @NotBlank(message = "El nombre del invitado es obligatorio")
    private String nombreInvitado;

    @Email(message = "El correo debe tener un formato válido")
    @NotBlank(message = "El correo del invitado es obligatorio")
    private String correoInvitado;

    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    public String getNombreInvitado() {
        return nombreInvitado;
    }

    public void setNombreInvitado(String nombreInvitado) {
        this.nombreInvitado = nombreInvitado;
    }

    public String getCorreoInvitado() {
        return correoInvitado;
    }

    public void setCorreoInvitado(String correoInvitado) {
        this.correoInvitado = correoInvitado;
    }
}

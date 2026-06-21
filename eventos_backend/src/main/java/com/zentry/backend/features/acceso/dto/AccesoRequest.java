package com.zentry.backend.features.acceso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AccesoRequest {

    @NotBlank(message = "El token QR es obligatorio")
    private String qrToken;

    @NotNull(message = "El ID del staff es obligatorio")
    private Long idStaff;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long idEvento;

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public Long getIdStaff() {
        return idStaff;
    }

    public void setIdStaff(Long idStaff) {
        this.idStaff = idStaff;
    }

    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }
}

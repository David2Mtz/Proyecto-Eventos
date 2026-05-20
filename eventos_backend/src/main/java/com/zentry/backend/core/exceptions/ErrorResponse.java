package com.zentry.backend.core.exceptions;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String mensaje;
    private int codigo;
    private String ruta;
    private LocalDateTime fecha;

    public ErrorResponse() {
    }

    public ErrorResponse(String mensaje, int codigo, String ruta, LocalDateTime fecha) {
        this.mensaje = mensaje;
        this.codigo = codigo;
        this.ruta = ruta;
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
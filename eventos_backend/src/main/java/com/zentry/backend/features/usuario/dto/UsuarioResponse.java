package com.zentry.backend.features.usuario.dto;

import com.zentry.backend.core.domain.Rol;
import com.zentry.backend.core.domain.Usuario;
import java.util.Set;
import java.util.stream.Collectors;

public class UsuarioResponse {

    private Long idUsuario;
    private String nombreDeUsuario;
    private String email;
    private Set<String> roles;
    private Boolean bloqueado;
    private Boolean habilitado;

    public UsuarioResponse() {
    }

    public UsuarioResponse(Long idUsuario, String nombreDeUsuario, String email, Set<String> roles, Boolean bloqueado, Boolean habilitado) {
        this.idUsuario = idUsuario;
        this.nombreDeUsuario = nombreDeUsuario;
        this.email = email;
        this.roles = roles;
        this.bloqueado = bloqueado;
        this.habilitado = habilitado;
    }

    public static UsuarioResponse fromEntity(Usuario usuario) {
        Set<String> nombresRoles = usuario.getRoles().stream()
                .map(Rol::getNombreRol)
                .collect(Collectors.toSet());

        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombreDeUsuario(),
                usuario.getEmail(),
                nombresRoles,
                usuario.getBloqueado(),
                usuario.getHabilitado()
        );
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
}

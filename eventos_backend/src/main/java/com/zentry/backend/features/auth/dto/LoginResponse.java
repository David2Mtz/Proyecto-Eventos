package com.zentry.backend.features.auth.dto;

import com.zentry.backend.core.domain.Rol;
import com.zentry.backend.core.domain.Usuario;
import java.util.Set;
import java.util.stream.Collectors;

public class LoginResponse {

    private String mensaje;
    private String token;
    private String tipoToken;
    private Long idUsuario;
    private String nombreDeUsuario;
    private String email;
    private Set<String> roles;

    public LoginResponse() {
    }

    public LoginResponse(String mensaje, String token, String tipoToken, Long idUsuario, String nombreDeUsuario, String email, Set<String> roles) {
        this.mensaje = mensaje;
        this.token = token;
        this.tipoToken = tipoToken;
        this.idUsuario = idUsuario;
        this.nombreDeUsuario = nombreDeUsuario;
        this.email = email;
        this.roles = roles;
    }

    public static LoginResponse fromEntity(Usuario usuario, String token) {
        Set<String> nombresRoles = usuario.getRoles().stream()
                .map(Rol::getNombreRol)
                .collect(Collectors.toSet());

        return new LoginResponse(
                "Inicio de sesión exitoso",
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNombreDeUsuario(),
                usuario.getEmail(),
                nombresRoles
        );
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getToken() {
        return token;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }
}

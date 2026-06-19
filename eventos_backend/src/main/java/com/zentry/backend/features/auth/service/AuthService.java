package com.zentry.backend.features.auth.service;

import com.zentry.backend.features.auth.dto.LoginRequest;
import com.zentry.backend.features.auth.dto.LoginResponse;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.core.exceptions.SolicitudInvalidaException;
import com.zentry.backend.features.usuario.repository.UsuarioRepository;
import com.zentry.backend.core.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new SolicitudInvalidaException("Usuario o contraseña incorrectos"));

        if (usuario.getBloqueado()) {
            throw new SolicitudInvalidaException("El usuario está bloqueado");
        }

        if (!usuario.getHabilitado()) {
            throw new SolicitudInvalidaException("El usuario está deshabilitado");
        }

        boolean passwordValida = passwordEncoder.matches(
                loginRequest.getPassword(),
                usuario.getClaveDeUsuario()
        );

        if (!passwordValida) {
            throw new SolicitudInvalidaException("Usuario o contraseña incorrectos");
        }

        String token = jwtService.generarToken(usuario);

        return LoginResponse.fromEntity(usuario, token);
    }
}

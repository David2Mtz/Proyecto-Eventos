package com.zentry.backend.features.usuario.service;

import com.zentry.backend.core.domain.Rol;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.features.usuario.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreDeUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nombre: " + username));

        String[] nombresRoles = usuario.getRoles().stream()
                .map(Rol::getNombreRol)
                .toArray(String[]::new);

        return User.builder()
                .username(usuario.getNombreDeUsuario())
                .password(usuario.getClaveDeUsuario())
                .roles(nombresRoles)
                .accountLocked(usuario.getBloqueado())
                .disabled(!usuario.getHabilitado())
                .build();
    }
}

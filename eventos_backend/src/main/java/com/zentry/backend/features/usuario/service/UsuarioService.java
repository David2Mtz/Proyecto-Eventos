package com.zentry.backend.features.usuario.service;

import com.zentry.backend.core.domain.Rol;
import com.zentry.backend.core.domain.Usuario;
import com.zentry.backend.core.exceptions.RecursoNoEncontradoException;
import com.zentry.backend.core.exceptions.SolicitudInvalidaException;
import com.zentry.backend.features.usuario.repository.RolRepository;
import com.zentry.backend.features.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }

    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreDeUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con nombre: " + nombreUsuario));
    }

    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByNombreDeUsuario(usuario.getNombreDeUsuario())) {
            throw new SolicitudInvalidaException("Ya existe un usuario con ese nombre de usuario");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new SolicitudInvalidaException("Ya existe un usuario con ese email");
        }

        usuario.setIdUsuario(null);
        usuario.setBloqueado(false);
        usuario.setHabilitado(true);

        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            Rol defaultRol = rolRepository.findByNombreRol("USER")
                    .orElseThrow(() -> new RecursoNoEncontradoException("Rol por defecto no encontrado"));
            Set<Rol> roles = new HashSet<>();
            roles.add(defaultRol);
            usuario.setRoles(roles);
        } else {
            Set<Rol> persistentRoles = new HashSet<>();
            for (Rol r : usuario.getRoles()) {
                String nombreRol = r.getNombreRol();
                if (nombreRol != null) {
                    Rol persistentRol = rolRepository.findByNombreRol(nombreRol)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado: " + nombreRol));
                    persistentRoles.add(persistentRol);
                }
            }
            usuario.setRoles(persistentRoles);
        }

        usuario.setClaveDeUsuario(passwordEncoder.encode(usuario.getClaveDeUsuario()));

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario usuario = buscarPorId(id);

        if (!usuario.getNombreDeUsuario().equals(usuarioActualizado.getNombreDeUsuario())
                && usuarioRepository.existsByNombreDeUsuario(usuarioActualizado.getNombreDeUsuario())) {
            throw new SolicitudInvalidaException("Ya existe otro usuario con ese nombre de usuario");
        }

        usuario.setNombreDeUsuario(usuarioActualizado.getNombreDeUsuario());
        usuario.setEmail(usuarioActualizado.getEmail());
        
        if (usuarioActualizado.getRoles() != null && !usuarioActualizado.getRoles().isEmpty()) {
            Set<Rol> persistentRoles = new HashSet<>();
            for (Rol r : usuarioActualizado.getRoles()) {
                String nombreRol = r.getNombreRol();
                if (nombreRol != null) {
                    Rol persistentRol = rolRepository.findByNombreRol(nombreRol)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado: " + nombreRol));
                    persistentRoles.add(persistentRol);
                }
            }
            usuario.setRoles(persistentRoles);
        }
        
        usuario.setBloqueado(usuarioActualizado.getBloqueado());
        usuario.setHabilitado(usuarioActualizado.getHabilitado());

        if (usuarioActualizado.getClaveDeUsuario() != null && !usuarioActualizado.getClaveDeUsuario().isBlank()) {
            usuario.setClaveDeUsuario(passwordEncoder.encode(usuarioActualizado.getClaveDeUsuario()));
        }

        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setHabilitado(false);
        usuarioRepository.save(usuario);
    }
}

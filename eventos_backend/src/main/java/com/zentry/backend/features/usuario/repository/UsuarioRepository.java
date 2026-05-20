package com.zentry.backend.features.usuario.repository;

import com.zentry.backend.core.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombreDeUsuario(String nombreDeUsuario);
    Optional<Usuario> findByEmail(String email);
    boolean existsByNombreDeUsuario(String nombreDeUsuario);
    boolean existsByEmail(String email);
}

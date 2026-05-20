package com.zentry.backend.features.usuario.repository;

import com.zentry.backend.core.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    java.util.Optional<Rol> findByNombreRol(String nombreRol);
}

package com.zentry.backend.features.invitado.repository;

import com.zentry.backend.core.domain.Invitado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitadoRepository extends JpaRepository<Invitado, Long> {
    Optional<Invitado> findByCorreo(String correo);
}

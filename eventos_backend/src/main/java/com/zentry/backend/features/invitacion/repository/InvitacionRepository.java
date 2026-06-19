package com.zentry.backend.features.invitacion.repository;

import com.zentry.backend.core.domain.Invitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {
    Optional<Invitacion> findByQrToken(String qrToken);
    List<Invitacion> findInvitacionsByIdEvento(Long idEvento);
}

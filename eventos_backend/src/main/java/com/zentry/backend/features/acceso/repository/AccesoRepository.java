package com.zentry.backend.features.acceso.repository;

import com.zentry.backend.core.domain.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long> {
    List<Acceso> findByInvitacionEventoIdEvento(Long idEvento);
}

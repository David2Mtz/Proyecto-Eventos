package com.zentry.backend.features.evento.repository;

import com.zentry.backend.core.domain.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByAnfitrionIdUsuario(Long idAnfitrion);
}

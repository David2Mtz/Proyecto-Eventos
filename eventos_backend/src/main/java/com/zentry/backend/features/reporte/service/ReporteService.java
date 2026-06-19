package com.zentry.backend.features.reporte.service;

import com.zentry.backend.core.domain.Acceso;
import com.zentry.backend.core.domain.EstadoInvitacion;
import com.zentry.backend.core.domain.Evento;
import com.zentry.backend.core.domain.Invitacion;
import com.zentry.backend.core.domain.Invitado;
import com.zentry.backend.features.acceso.repository.AccesoRepository;
import com.zentry.backend.features.evento.repository.EventoRepository;
import com.zentry.backend.core.exceptions.RecursoNoEncontradoException;
import com.zentry.backend.features.invitacion.repository.InvitacionRepository;
import com.zentry.backend.features.invitado.repository.InvitadoRepository;
import com.zentry.backend.features.reporte.dto.ReporteAsistenciaDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {

    private final AccesoRepository accesoRepository;
    private final EventoRepository eventoRepository;
    private final InvitacionRepository invitacionRepository;
    private final InvitadoRepository invitadoRepository;

    public ReporteService(AccesoRepository accesoRepository,
                          EventoRepository eventoRepository,
                          InvitacionRepository invitacionRepository,
                          InvitadoRepository invitadoRepository) {
        this.accesoRepository = accesoRepository;
        this.eventoRepository = eventoRepository;
        this.invitacionRepository = invitacionRepository;
        this.invitadoRepository = invitadoRepository;
    }

    public List<ReporteAsistenciaDTO> obtenerAsistenciaEvento(Long idEvento) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + idEvento));

        List<Invitacion> invitacionesUtilizadas = invitacionRepository.findInvitacionsByIdEvento(idEvento)
                .stream()
                .filter(inv -> inv.getEstado() == EstadoInvitacion.UTILIZADO)
                .toList();

        return invitacionesUtilizadas.stream().map(invitacion -> {
            Invitado invitado = invitadoRepository.findById(invitacion.getIdInvitado())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Invitado no encontrado con ID: " + invitacion.getIdInvitado()));

            List<Acceso> accesos = accesoRepository.findAllByIdInvitacion(invitacion.getIdInvitacion());
            LocalDateTime fechaHoraEntrada = accesos.isEmpty() ? null : accesos.get(0).getFechaHoraEntrada();

            return ReporteAsistenciaDTO.builder()
                    .nombre(invitado.getNombre())
                    .correo(invitado.getCorreo())
                    .fechaHoraEntrada(fechaHoraEntrada)
                    .build();
        }).toList();
    }
}

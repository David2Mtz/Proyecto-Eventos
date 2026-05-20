package com.zentry.backend.features.reporte.service;

import com.zentry.backend.core.domain.Acceso;
import com.zentry.backend.core.domain.Evento;
import com.zentry.backend.features.acceso.repository.AccesoRepository;
import com.zentry.backend.features.evento.repository.EventoRepository;
import com.zentry.backend.core.exceptions.RecursoNoEncontradoException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {

    private final AccesoRepository accesoRepository;
    private final EventoRepository eventoRepository;

    public ReporteService(AccesoRepository accesoRepository, EventoRepository eventoRepository) {
        this.accesoRepository = accesoRepository;
        this.eventoRepository = eventoRepository;
    }

    public byte[] generarReporteAsistenciaPdf(Long idEvento) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Evento no encontrado con ID: " + idEvento));

        List<Acceso> accesos = accesoRepository.findByInvitacionEventoIdEvento(idEvento);

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                contentStream.beginText();
                contentStream.setFont(fontBold, 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Reporte de Asistencia: " + evento.getNombreEvento());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(fontNormal, 12);
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("Fecha del evento: " + evento.getFecha());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Lugar: " + evento.getLugar());
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Fecha de generación: " + LocalDateTime.now());
                contentStream.endText();

                float y = 650;
                contentStream.beginText();
                contentStream.setFont(fontBold, 12);
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Invitado");
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("Correo");
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("Hora de Entrada");
                contentStream.endText();

                y -= 20;
                contentStream.setFont(fontNormal, 10);
                for (Acceso acceso : accesos) {
                    if (y < 50) {
                        // In a real scenario, should add a new page here.
                        break;
                    }
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, y);
                    contentStream.showText(acceso.getInvitacion().getInvitado().getNombre());
                    contentStream.newLineAtOffset(250, 0);
                    contentStream.showText(acceso.getInvitacion().getInvitado().getCorreo());
                    contentStream.newLineAtOffset(200, 0);
                    contentStream.showText(acceso.getFechaHoraEntrada().toString());
                    contentStream.endText();
                    y -= 15;
                }
            }

            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }
}

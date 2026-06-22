import { Injectable, inject, signal, computed } from '@angular/core';
import { jsPDF } from 'jspdf';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, tap } from 'rxjs';

export interface Invitacion {
  idInvitacion: number;
  evento: {
    idEvento: number;
    nombreEvento: string;
  };
  invitado: {
    nombre: string;
    correo: string;
  };
  qrToken: string;
  estado: 'PENDIENTE' | 'CONFIRMADO' | 'CANCELADO' | 'UTILIZADO';
}

@Injectable({
  providedIn: 'root'
})
export class InvitacionService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/invitaciones`;

  private _invitaciones = signal<Invitacion[]>([]);
  private _loading = signal<boolean>(false);

  invitaciones = computed(() => this._invitaciones());
  loading = computed(() => this._loading());

  cargarPorEvento(idEvento: number): void {
    this._loading.set(true);
    this.http.get<Invitacion[]>(`${this.apiUrl}/evento/${idEvento}`).subscribe({
      next: (data) => {
        this._invitaciones.set(data);
        this._loading.set(false);
      },
      error: () => this._loading.set(false)
    });
  }

  crearInvitacion(idEvento: number, nombre: string, correo: string): Observable<Invitacion> {
    return this.http.post<Invitacion>(this.apiUrl, {
      idEvento,
      nombreInvitado: nombre,
      correoInvitado: correo
    }).pipe(
      tap((nueva) => {
        this._invitaciones.update(prev => [...prev, nueva]);
      })
    );
  }

  eliminarInvitacion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        this._invitaciones.update(prev => prev.filter(i => i.idInvitacion !== id));
      })
    );
  }

  registrarAcceso(qrToken: string, idStaff: number, idEvento: number): Observable<any> {
    return this.http.post(`${environment.apiUrl}/accesos/validar`, { qrToken, idStaff, idEvento }).pipe(
      tap(() => {
        // Actualizar estado local si es necesario
      })
    );
  }

  descargarReportePdf(nombreEvento: string, lista: Invitacion[]): void {
    const doc = new jsPDF();
    
    // Configuración de Colores y Tipografía
    // Título Principal
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(20);
    doc.setTextColor(30, 41, 59); // Slate-800
    doc.text('Reporte de Asistencia', 14, 20);
    
    // Subtítulo
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(14);
    doc.setTextColor(99, 102, 241); // Indigo-500
    doc.text(`Evento: ${nombreEvento}`, 14, 28);
    
    // Fecha y hora de generación
    doc.setFontSize(9);
    doc.setTextColor(148, 163, 184); // Slate-400
    const fechaGen = new Date().toLocaleString();
    doc.text(`Generado el: ${fechaGen}`, 14, 34);
    
    // Separador
    doc.setDrawColor(226, 232, 240); // Slate-200
    doc.line(14, 38, 196, 38);
    
    // Estadísticas
    const total = lista.length;
    const ingresados = lista.filter(i => i.estado === 'UTILIZADO').length;
    const pendientes = lista.filter(i => i.estado === 'PENDIENTE').length;
    
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(10);
    doc.setTextColor(71, 85, 105); // Slate-600
    doc.text(`Total Invitados: ${total}`, 14, 46);
    doc.text(`Ingresados: ${ingresados}`, 70, 46);
    doc.text(`Pendientes: ${pendientes}`, 130, 46);
    
    doc.setDrawColor(226, 232, 240); // Slate-200
    doc.line(14, 50, 196, 50);
    
    // Tabla Cabecera
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(11);
    doc.setTextColor(15, 23, 42); // Slate-900
    doc.text('Nombre', 14, 58);
    doc.text('Correo', 80, 58);
    doc.text('Estado', 150, 58);
    
    doc.setDrawColor(148, 163, 184); // Slate-400
    doc.line(14, 61, 196, 61);
    
    // Cuerpo de la Tabla
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(10);
    doc.setTextColor(51, 65, 85); // Slate-700
    
    let y = 68;
    const pageHeight = doc.internal.pageSize.height;
    
    lista.forEach((inv) => {
      // Control de salto de página
      if (y > pageHeight - 20) {
        doc.addPage();
        // Reimprimir cabecera de tabla
        doc.setFont('helvetica', 'bold');
        doc.setFontSize(11);
        doc.setTextColor(15, 23, 42);
        doc.text('Nombre', 14, 20);
        doc.text('Correo', 80, 20);
        doc.text('Estado', 150, 20);
        doc.setDrawColor(148, 163, 184);
        doc.line(14, 23, 196, 23);
        
        doc.setFont('helvetica', 'normal');
        doc.setFontSize(10);
        doc.setTextColor(51, 65, 85);
        y = 30;
      }
      
      const nombre = inv.invitado?.nombre || 'N/A';
      const correo = inv.invitado?.correo || 'N/A';
      const estado = inv.estado || 'PENDIENTE';
      
      doc.text(nombre, 14, y);
      doc.text(correo, 80, y);
      doc.text(estado, 150, y);
      
      // Línea divisoria suave entre filas
      doc.setDrawColor(241, 245, 249); // Slate-100
      doc.line(14, y + 3, 196, y + 3);
      y += 8;
    });
    
    // Guardar el archivo
    doc.save(`reporte-asistencia-${nombreEvento.toLowerCase().replace(/[^a-z0-9]/g, '-')}.pdf`);
  }
}

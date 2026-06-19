import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InvitacionService } from '../../../services/invitacion.service';
import { EventoService } from '../../../services/evento.service';
import { AuthService } from '../../../services/auth.service';
import Swal from 'sweetalert2';
import { ZXingScannerModule } from '@zxing/ngx-scanner';
import { BarcodeFormat } from '@zxing/library';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, ZXingScannerModule],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.scss'
})
export class StaffDashboardComponent implements OnInit {
  private invitacionService = inject(InvitacionService);
  private eventoService = inject(EventoService);
  private authService = inject(AuthService);

  eventos = this.eventoService.eventos;
  invitaciones = this.invitacionService.invitaciones;
  
  idEventoSeleccionado = signal<number | null>(null);
  qrToken = '';
  isLoading = signal(false);
  scanState = signal<'idle' | 'validating' | 'success' | 'error'>('idle');
  scanErrorMessage = signal<string>('');

  // QR Scanner state
  allowedFormats = [BarcodeFormat.QR_CODE];
  scannerEnabled = signal(false);
  hasDevices = signal(false);
  hasPermission = signal(false);

  ngOnInit() {
    this.eventoService.cargarEventos();
  }

  seleccionarEvento(id: number) {
    this.idEventoSeleccionado.set(id);
    this.invitacionService.cargarPorEvento(id);
    this.scannerEnabled.set(true);
    this.scanState.set('idle');
  }

  handleQrCodeResult(resultString: string) {
    console.log('QR detectado:', resultString);
    if (this.scanState() !== 'idle') return;
    this.qrToken = resultString;
    this.scannerEnabled.set(false); // Pause scanner immediately!
    this.validarAcceso();
  }

  onHasDevices(has: boolean) {
    this.hasDevices.set(has);
  }

  onPermissionResponse(permission: boolean) {
    this.hasPermission.set(permission);
  }

  validarAccesoManual() {
    if (!this.qrToken || this.scanState() === 'validating') return;
    this.scannerEnabled.set(false); // Pause scanner!
    this.validarAcceso();
  }

  validarAcceso() {
    const user = this.authService.currentUser();
    if (!user || !this.qrToken) {
      this.continuarEscaneo();
      return;
    }

    this.scanState.set('validating');
    this.isLoading.set(true);
    this.invitacionService.registrarAcceso(this.qrToken, user.idUsuario).subscribe({
      next: () => {
        this.scanState.set('success');
        this.qrToken = '';
        this.isLoading.set(false);
        if (this.idEventoSeleccionado()) {
          this.invitacionService.cargarPorEvento(this.idEventoSeleccionado()!);
        }
        // Auto reset after 3 seconds
        setTimeout(() => {
          if (this.scanState() === 'success') {
            this.continuarEscaneo();
          }
        }, 3000);
      },
      error: (err) => {
        this.scanErrorMessage.set(err.error?.mensaje || 'No se pudo validar la invitación');
        this.scanState.set('error');
        this.isLoading.set(false);
        this.qrToken = '';
      }
    });
  }

  continuarEscaneo() {
    this.scanState.set('idle');
    this.scannerEnabled.set(true);
  }

  imprimirPdf() {
    const id = this.idEventoSeleccionado();
    if (id) {
      const ev = this.eventos().find(e => e.idEvento === id);
      const nombreEvento = ev ? ev.nombreEvento : 'Evento';
      this.invitacionService.descargarReportePdf(nombreEvento, this.invitaciones());
    }
  }
}

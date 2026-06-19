import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Evento, EventoService } from '../../../services/evento.service';
import { AuthService } from '../../../services/auth.service';
import { InvitacionService } from '../../../services/invitacion.service';
import { FormsModule } from '@angular/forms';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-anfitrion-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './anfitrion-dashboard.component.html',
  styleUrl: './anfitrion-dashboard.component.scss'
})
export class AnfitrionDashboardComponent implements OnInit {
  private eventoService = inject(EventoService);
  private authService = inject(AuthService);
  private invitacionService = inject(InvitacionService);

  eventos = this.eventoService.eventos;
  loading = this.eventoService.loading;
  invitaciones = this.invitacionService.invitaciones;

  // Formulario simple para crear evento
  nuevoEvento = {
    nombreEvento: '',
    lugar: '',
    fecha: ''
  };

  // Estado para edición
  editando = signal<boolean>(false);
  eventoAEditar = {
    idEvento: 0,
    nombreEvento: '',
    lugar: '',
    fecha: ''
  };

  // Gestión de Invitados
  verInvitados = signal<boolean>(false);
  eventoSeleccionado = signal<Evento | null>(null);
  isAddingInvitado = signal<boolean>(false);
  nuevoInvitado = {
    nombre: '',
    correo: ''
  };

  ngOnInit() {
    const user = this.authService.currentUser();
    if (user) {
      this.eventoService.cargarPorAnfitrion(user.idUsuario);
    }
  }

  crear() {
    const user = this.authService.currentUser();
    if (!user) return;

    const formData = new FormData();
    formData.append('nombreEvento', this.nuevoEvento.nombreEvento);
    formData.append('lugar', this.nuevoEvento.lugar);
    formData.append('fecha', this.nuevoEvento.fecha);
    formData.append('idAnfitrion', user.idUsuario.toString());

    this.eventoService.crearEvento(formData).subscribe({
      next: () => {
        Swal.fire({ title: 'Evento creado', text: 'El evento se ha creado exitosamente.', icon: 'success', confirmButtonColor: '#6366f1' });
        this.nuevoEvento = { nombreEvento: '', lugar: '', fecha: '' };
      }
    });
  }

  editar(evento: Evento) {
    this.editando.set(true);
    this.eventoAEditar = {
      idEvento: evento.idEvento || 0,
      nombreEvento: evento.nombreEvento,
      lugar: evento.lugar,
      fecha: evento.fecha
    };
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelarEdicion() {
    this.editando.set(false);
    this.eventoAEditar = { idEvento: 0, nombreEvento: '', lugar: '', fecha: '' };
  }

  actualizar() {
    if (!this.eventoAEditar.idEvento) return;

    const formData = new FormData();
    formData.append('nombreEvento', this.eventoAEditar.nombreEvento);
    formData.append('lugar', this.eventoAEditar.lugar);
    formData.append('fecha', this.eventoAEditar.fecha);

    this.eventoService.actualizarEvento(this.eventoAEditar.idEvento, formData).subscribe({
      next: () => {
        Swal.fire({ title: 'Evento actualizado', text: 'El evento se ha actualizado exitosamente.', icon: 'success', confirmButtonColor: '#6366f1' });
        this.cancelarEdicion();
      },
      error: () => {
        Swal.fire({ title: 'Error', text: 'Error al actualizar el evento.', icon: 'error', confirmButtonColor: '#ef4444' });
      }
    });
  }

  eliminar(id: number) {
    Swal.fire({
      title: '¿Eliminar este evento?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#64748b',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.eventoService.eliminarEvento(id).subscribe(() => {
          Swal.fire({ title: 'Evento eliminado', icon: 'success', confirmButtonColor: '#6366f1' });
        });
      }
    });
  }

  // --- Invitados ---
  abrirInvitados(evento: Evento) {
    this.eventoSeleccionado.set(evento);
    this.verInvitados.set(true);
    this.invitacionService.cargarPorEvento(evento.idEvento!);
  }

  cerrarInvitados() {
    this.verInvitados.set(false);
    this.eventoSeleccionado.set(null);
  }

  agregarInvitado() {
    const ev = this.eventoSeleccionado();
    if (!ev || !this.nuevoInvitado.nombre || !this.nuevoInvitado.correo) return;

    this.isAddingInvitado.set(true);
    this.invitacionService.crearInvitacion(
      ev.idEvento!,
      this.nuevoInvitado.nombre,
      this.nuevoInvitado.correo
    ).subscribe({
      next: () => {
        Swal.fire({
          title: 'Invitación enviada',
          text: 'Se ha registrado el invitado y enviado el correo de acceso.',
          icon: 'success',
          confirmButtonColor: '#6366f1'
        });
        this.nuevoInvitado = { nombre: '', correo: '' };
        this.isAddingInvitado.set(false);
      },
      error: (err) => {
        Swal.fire({
          title: 'Error',
          text: err.error?.mensaje || 'Error al enviar invitación',
          icon: 'error',
          confirmButtonColor: '#ef4444'
        });
        this.isAddingInvitado.set(false);
      }
    });
  }

  quitarInvitado(id: number) {
    Swal.fire({
      title: '¿Quitar invitado?',
      text: 'Se eliminará la invitación de este usuario.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      cancelButtonColor: '#64748b',
      confirmButtonText: 'Sí, quitar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.invitacionService.eliminarInvitacion(id).subscribe(() => {
          Swal.fire({ title: 'Invitado quitado', icon: 'success', confirmButtonColor: '#6366f1' });
        });
      }
    });
  }

  descargarListaAcceso() {
    const ev = this.eventoSeleccionado();
    if (ev) {
      this.invitacionService.descargarReportePdf(ev.nombreEvento, this.invitaciones());
    }
  }
}

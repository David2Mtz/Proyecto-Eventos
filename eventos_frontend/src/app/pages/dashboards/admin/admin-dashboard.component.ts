import { Component, inject, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService, Usuario } from '../../../services/usuario.service';
import { EventoService, Evento } from '../../../services/evento.service';
import { InvitacionService } from '../../../services/invitacion.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss'
})
export class AdminDashboardComponent implements OnInit {
  private usuarioService = inject(UsuarioService);
  private eventoService = inject(EventoService);
  private invitacionService = inject(InvitacionService);

  // Signals
  eventos = this.eventoService.eventos;
  usuarios = this.usuarioService.usuarios;
  loading = this.usuarioService.loading;
  invitaciones = this.invitacionService.invitaciones;

  // Filtros
  anfitriones = computed(() => 
    this.usuarios().filter(u => u.roles?.includes('ANFITRION'))
  );

  staff = computed(() => 
    this.usuarios().filter(u => u.roles?.includes('STAFF'))
  );

  // Forms Visibility
  showEventForm = signal(false);
  showUserForm = signal(false);
  editMode = signal(false);

  // Event Form Model
  eventForm = {
    idEvento: 0,
    nombreEvento: '',
    lugar: '',
    fecha: '',
    idAnfitrion: ''
  };

  // User Form Model
  userForm = {
    idUsuario: 0,
    nombre: '',
    correo: '',
    nombreDeUsuario: '',
    claveDeUsuario: '',
    rol: 'ANFITRION'
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
    this.usuarioService.cargarUsuarios();
    this.eventoService.cargarEventos();
  }

  // --- Event Methods ---
  openCreateEvent() {
    this.editMode.set(false);
    this.eventForm = { idEvento: 0, nombreEvento: '', lugar: '', fecha: '', idAnfitrion: '' };
    this.showEventForm.set(true);
  }

  editarEvento(evento: Evento) {
    this.editMode.set(true);
    this.eventForm = {
      idEvento: evento.idEvento!,
      nombreEvento: evento.nombreEvento,
      lugar: evento.lugar,
      fecha: evento.fecha,
      idAnfitrion: evento.anfitrion?.idUsuario?.toString() || ''
    };
    this.showEventForm.set(true);
  }

  guardarEvento() {
    const formData = new FormData();
    formData.append('nombreEvento', this.eventForm.nombreEvento);
    formData.append('lugar', this.eventForm.lugar);
    formData.append('fecha', this.eventForm.fecha);
    formData.append('idAnfitrion', this.eventForm.idAnfitrion);

    if (this.editMode()) {
      this.eventoService.actualizarEvento(this.eventForm.idEvento, formData).subscribe(() => {
        Swal.fire({ title: 'Evento actualizado', icon: 'success', confirmButtonColor: '#6366f1' });
        this.showEventForm.set(false);
      });
    } else {
      this.eventoService.crearEvento(formData).subscribe(() => {
        Swal.fire({ title: 'Evento creado', icon: 'success', confirmButtonColor: '#6366f1' });
        this.showEventForm.set(false);
      });
    }
  }

  eliminarEvento(id: number) {
    Swal.fire({
      title: '¿Seguro que deseas eliminar este evento?',
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

  deshabilitarEvento(id: number) {
    this.eventoService.deshabilitarEvento(id).subscribe();
  }

  // --- User Methods ---
  openCreateUser() {
    this.userForm = { idUsuario: 0, nombre: '', correo: '', nombreDeUsuario: '', claveDeUsuario: '', rol: 'ANFITRION' };
    this.showUserForm.set(true);
  }

  guardarUsuario() {
    const nuevoUsuario: any = {
      nombre: this.userForm.nombre,
      email: this.userForm.correo,
      nombreDeUsuario: this.userForm.nombreDeUsuario,
      claveDeUsuario: this.userForm.claveDeUsuario,
      roles: [this.userForm.rol]
    };

    this.usuarioService.crear(nuevoUsuario).subscribe(() => {
      Swal.fire({ title: 'Usuario creado', icon: 'success', confirmButtonColor: '#6366f1' });
      this.showUserForm.set(false);
      this.usuarioService.cargarUsuarios();
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


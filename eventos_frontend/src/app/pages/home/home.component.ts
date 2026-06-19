import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { EventoService } from '../../services/evento.service';
import { UsuarioService } from '../../services/usuario.service';
import { InvitacionService } from '../../services/invitacion.service';
import { AnfitrionDashboardComponent } from '../dashboards/anfitrion/anfitrion-dashboard.component';
import { AdminDashboardComponent } from '../dashboards/admin/admin-dashboard.component';
import { StaffDashboardComponent } from '../dashboards/staff/staff-dashboard.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, 
    RouterLink, 
    AnfitrionDashboardComponent, 
    AdminDashboardComponent, 
    StaffDashboardComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  authService = inject(AuthService);
  private eventoService = inject(EventoService);
  private usuarioService = inject(UsuarioService);

  // User state
  user = this.authService.currentUser;
  
  // Welcome data
  welcomeMessage = computed(() => {
    const u = this.user();
    if (!u) return 'Bienvenido a Zentry Eventos';
    return `¡Hola de nuevo, ${u.nombreDeUsuario}!`;
  });

  roleDescription = computed(() => {
    const roles = this.user()?.roles || [];
    if (roles.includes('ADMIN')) {
      return 'Tienes acceso total al sistema. Puedes gestionar usuarios, supervisar todos los eventos y configurar los parámetros globales.';
    } else if (roles.includes('ANFITRION')) {
      return 'Desde aquí puedes crear nuevos eventos, gestionar tus listas de invitados y ver el estado de tus celebraciones en tiempo real.';
    } else if (roles.includes('STAFF')) {
      return 'Tu rol es fundamental para el control de acceso. Selecciona un evento para comenzar a validar invitaciones mediante QR.';
    }
    return 'La plataforma definitiva para la gestión y control de asistencia a tus eventos más importantes.';
  });

  ngOnInit() {
    // If user is logged in, we might want to pre-load some data
    if (this.authService.isLoggedIn()) {
      // Basic initialization if needed
    }
  }

  hasRole(role: string): boolean {
    return this.user()?.roles.includes(role) || false;
  }
}

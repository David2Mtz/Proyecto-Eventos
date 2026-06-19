import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-dashboard-dispatcher',
  standalone: true,
  template: '<div class="loading">Redirigiendo a tu panel...</div>',
  styles: ['.loading { display: flex; justify-content: center; align-items: center; height: 50vh; font-weight: 600; color: var(--text-muted); }']
})
export class DashboardDispatcherComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit() {
    const user = this.authService.currentUser();
    console.log('Dispatcher: Usuario actual detectado:', user);
    
    if (!user) {
      console.warn('Dispatcher: No hay usuario en el signal, redirigiendo a login');
      this.router.navigate(['/login']);
      return;
    }

    if (!user.roles || user.roles.length === 0) {
      console.error('Dispatcher: El usuario no tiene roles asignados:', user);
      this.router.navigate(['/']);
      return;
    }

    // Convertimos todos los roles a mayúsculas para comparar
    const userRoles = user.roles.map(r => r.toUpperCase());
    console.log('Dispatcher: Roles detectados:', userRoles);

    if (userRoles.includes('ADMIN')) {
      this.router.navigate(['/admin']);
    } else if (userRoles.includes('ANFITRION')) {
      this.router.navigate(['/anfitrion']);
    } else if (userRoles.includes('STAFF')) {
      this.router.navigate(['/staff']);
    } else {
      console.warn('Dispatcher: Ningún rol reconocido para navegación:', userRoles);
      this.router.navigate(['/']);
    }
  }
}

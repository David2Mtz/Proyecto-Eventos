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
    
    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    switch (user.rol) {
      case 'ADMIN':
        this.router.navigate(['/admin']);
        break;
      case 'ANFITRION':
        this.router.navigate(['/anfitrion']);
        break;
      case 'STAFF':
        this.router.navigate(['/staff']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }
}

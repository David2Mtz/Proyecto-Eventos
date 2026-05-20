import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.scss'
})
export class StaffDashboardComponent {
  private http = inject(HttpClient);
  private authService = inject(AuthService);

  qrToken = '';
  validationResult = signal<any>(null);
  errorMessage = signal<string | null>(null);
  isLoading = signal(false);

  validarAcceso() {
    if (!this.qrToken) return;

    this.isLoading.set(true);
    this.validationResult.set(null);
    this.errorMessage.set(null);

    const request = {
      qrToken: this.qrToken,
      idStaff: this.authService.currentUser()?.idUsuario
    };

    this.http.post<any>(`${environment.apiUrl}/accesos/validar`, request).subscribe({
      next: (res) => {
        this.validationResult.set(res);
        this.isLoading.set(false);
        this.qrToken = '';
      },
      error: (err) => {
        this.errorMessage.set(err.error?.mensaje || 'Token inválido o ya utilizado.');
        this.isLoading.set(false);
      }
    });
  }
}

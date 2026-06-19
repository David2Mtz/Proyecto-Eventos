import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  errorMessage = signal<string | null>(null);
  isLoading = signal(false);

  onSubmit() {
    if (!this.email || !this.password) {
      this.errorMessage.set('Por favor, completa todos los campos.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (response) => {
        console.log('Login exitoso, redirigiendo a Home...', response);
        this.router.navigate(['/']).then(navigated => {
          if (navigated) {
            console.log('Navegación a Home exitosa');
          } else {
            console.error('Navegación a Home fallida');
          }
        });
      },
      error: (err) => {
        console.error('Error de login:', err);
        this.errorMessage.set(err.error?.mensaje || 'Credenciales incorrectas o error de servidor.');
        this.isLoading.set(false);
      }
    });
  }
}

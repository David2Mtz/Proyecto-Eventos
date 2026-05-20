import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, tap } from 'rxjs';

export interface LoginResponse {
  mensaje: string;
  token: string;
  tipoToken: string;
  idUsuario: number;
  nombre: string;
  correo: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;
  
  // Signal to track user state
  currentUser = signal<LoginResponse | null>(null);

  constructor() {
    this.checkLocalStorage();
  }

  login(credentials: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        this.saveSession(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('zentry_session');
    this.currentUser.set(null);
  }

  private saveSession(response: LoginResponse): void {
    localStorage.setItem('zentry_session', JSON.stringify(response));
    this.currentUser.set(response);
  }

  private checkLocalStorage(): void {
    const session = localStorage.getItem('zentry_session');
    if (session) {
      try {
        this.currentUser.set(JSON.parse(session));
      } catch (e) {
        localStorage.removeItem('zentry_session');
      }
    }
  }

  isLoggedIn(): boolean {
    return !!this.currentUser();
  }

  getToken(): string | null {
    return this.currentUser()?.token || null;
  }
}

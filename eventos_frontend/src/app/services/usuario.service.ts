import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export interface Usuario {
  idUsuario: number;
  nombreDeUsuario: string;
  email: string;
  roles: string[];
  bloqueado: boolean;
  habilitado: boolean;
  // Aliases for compatibility if needed
  nombre?: string; 
  correo?: string;
  activo?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/usuarios`;

  private _usuarios = signal<Usuario[]>([]);
  private _loading = signal<boolean>(false);

  usuarios = computed(() => this._usuarios());
  loading = computed(() => this._loading());

  constructor() {}

  cargarUsuarios(): void {
    this._loading.set(true);
    this.http.get<Usuario[]>(this.apiUrl).subscribe({
      next: (data) => {
        // Map backend data to local structure if names differ
        const normalized = data.map(u => ({
          ...u,
          nombre: u.nombreDeUsuario,
          correo: u.email,
          activo: u.habilitado && !u.bloqueado
        }));
        this._usuarios.set(normalized);
        this._loading.set(false);
      },
      error: (err) => {
        console.error('Error:', err);
        this._loading.set(false);
      }
    });
  }

  crear(usuario: any): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, usuario);
  }
}

import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, tap } from 'rxjs';

export interface Evento {
  idEvento?: number;
  nombreEvento: string;
  fecha: string;
  lugar: string;
  imagenUrl?: string;
  anfitrion?: {
    idUsuario: number;
    nombre: string;
    nombreDeUsuario?: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class EventoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/eventos`;

  // State with Signals
  private _eventos = signal<Evento[]>([]);
  private _loading = signal<boolean>(false);
  private _error = signal<string | null>(null);

  // Public read-only signals
  eventos = computed(() => this._eventos());
  loading = computed(() => this._loading());
  error = computed(() => this._error());

  constructor() {
    // No cargar automáticamente para evitar 403 cuando no hay sesión
  }

  cargarEventos(): void {
    this._loading.set(true);
    this._error.set(null);

    this.http.get<Evento[]>(this.apiUrl).subscribe({
      next: (data) => {
        this._eventos.set(data);
        this._loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando eventos:', err);
        this._error.set('No se pudieron cargar los eventos.');
        this._loading.set(false);
      }
    });
  }

  cargarPorAnfitrion(idAnfitrion: number): void {
    this._loading.set(true);
    this.http.get<Evento[]>(`${this.apiUrl}/anfitrion/${idAnfitrion}`).subscribe({
      next: (data) => {
        this._eventos.set(data);
        this._loading.set(false);
      },
      error: (err) => {
        this._error.set('Error al cargar tus eventos.');
        this._loading.set(false);
      }
    });
  }

  crearEvento(formData: FormData): Observable<Evento> {
    return this.http.post<Evento>(this.apiUrl, formData).pipe(
      tap((nuevoEvento) => {
        // Update local state reactively
        this._eventos.update((prev) => [...prev, nuevoEvento]);
      })
    );
  }

  deshabilitarEvento(id: number): Observable<void> {
    // Asumiendo que existe un endpoint de patch o put para esto
    return this.http.patch<void>(`${this.apiUrl}/${id}/deshabilitar`, {}).pipe(
      tap(() => {
        // Podríamos marcarlo como deshabilitado localmente si el objeto Evento lo tuviera
        this.cargarEventos(); 
      })
    );
  }

  actualizarEvento(id: number, formData: FormData): Observable<Evento> {
    return this.http.put<Evento>(`${this.apiUrl}/${id}`, formData).pipe(
      tap((actualizado) => {
        this._eventos.update(prev => prev.map(e => e.idEvento === id ? actualizado : e));
      })
    );
  }

  eliminarEvento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        this._eventos.update(prev => prev.filter(e => e.idEvento !== id));
      })
    );
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Empresa, EmpresaResumo } from '../models/empresa.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class EmpresaService {
  private readonly url = `${environment.apiUrl}/empresas`;

  constructor(private http: HttpClient) {}

  listar(): Observable<EmpresaResumo[]> {
    return this.http.get<EmpresaResumo[]>(this.url);
  }

  buscarPorId(id: number): Observable<Empresa> {
    return this.http.get<Empresa>(`${this.url}/${id}`);
  }

  criar(empresa: Empresa): Observable<Empresa> {
    return this.http.post<Empresa>(this.url, empresa);
  }

  atualizar(id: number, empresa: Empresa): Observable<Empresa> {
    return this.http.put<Empresa>(`${this.url}/${id}`, empresa);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fornecedor, FornecedorResumo } from '../models/fornecedor.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FornecedorService {
  private readonly url = `${environment.apiUrl}/fornecedores`;

  constructor(private http: HttpClient) {}

  listar(): Observable<FornecedorResumo[]> {
    return this.http.get<FornecedorResumo[]>(this.url);
  }

  buscarPorId(id: number): Observable<Fornecedor> {
    return this.http.get<Fornecedor>(`${this.url}/${id}`);
  }

  filtrar(nome?: string, cpfCnpj?: string): Observable<FornecedorResumo[]> {
    let params = new HttpParams();
    if (nome) params = params.set('nome', nome);
    if (cpfCnpj) params = params.set('cpfCnpj', cpfCnpj);
    return this.http.get<FornecedorResumo[]>(`${this.url}/filtro`, { params });
  }

  criar(fornecedor: Fornecedor): Observable<Fornecedor> {
    return this.http.post<Fornecedor>(this.url, fornecedor);
  }

  atualizar(id: number, fornecedor: Fornecedor): Observable<Fornecedor> {
    return this.http.put<Fornecedor>(`${this.url}/${id}`, fornecedor);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CepResponse } from '../models/cep.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CepService {
  constructor(private http: HttpClient) {}

  consultar(cep: string): Observable<CepResponse> {
    const cepLimpo = cep.replace(/\D/g, '');
    return this.http.get<CepResponse>(`${environment.apiUrl}/cep/${cepLimpo}`);
  }
}

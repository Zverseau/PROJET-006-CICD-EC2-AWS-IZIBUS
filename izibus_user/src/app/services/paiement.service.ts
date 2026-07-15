import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';

const API_URL = `${environment.apiUrl}/v1/paiements`;

@Injectable({
  providedIn: 'root'
})
export class PaiementService {

  constructor(private http: HttpClient) { }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token') || '';
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  getChiffreAffaireStatsByCompagnie(compagnieId: number): Observable<any> {
    return this.http.get(`${API_URL}/compagnie/${compagnieId}/stats`, { headers: this.authHeaders() });
  }

}

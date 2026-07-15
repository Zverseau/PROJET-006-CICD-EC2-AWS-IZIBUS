import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../environment/environment';

const API_URL = `${environment.apiUrl}/v1/trajets`;

@Injectable({
  providedIn: 'root'
})
export class TrajetService {

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  createTrajet(trajetData: any): Observable<any> {
    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post(`${API_URL}/ajouter`, trajetData, { headers });
  }

  getTrajetsByCompagnie(compagnieId: number): Observable<any> {
    return this.http.get(`${API_URL}/compagnie/${compagnieId}`);
  }

  updateTrajet(id: number, trajetData: any): Observable<any> {
    const token = localStorage.getItem('access_token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put(`${API_URL}/modifier/${id}`, trajetData, { headers });
  }

  deleteTrajet(id: number): Observable<string> {
  const token = localStorage.getItem('access_token');
  const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  return this.http.delete(
    `${API_URL}/supprimer/${id}`,
    {
      headers,
      responseType: 'text'  // ← on attend du texte, pas du JSON
    }
  );

  }

  getTrajetById(id: number): Observable<any> {
    // Solution de repli si l'intercepteur échoue
    const token = localStorage.getItem('access_token');
    let headers = new HttpHeaders();

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return this.http.get(`${API_URL}/${id}`, { headers });
  }

}

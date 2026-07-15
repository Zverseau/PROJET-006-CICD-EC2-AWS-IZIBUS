import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

const API_URL = 'http://localhost:8080/api/v1/compagnies';

@Injectable({
  providedIn: 'root'
})
export class CompagnieService {
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token') || '';
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  private compagnieCache: { [id: number]: any } = {};


  getCompagnieById(id: number): Observable<any> {
    return this.http.get(`${API_URL}/${id}`, { 
      headers: this.authHeaders(),
      responseType: 'json'
    });
  }

  getAllCompagnies(): Observable<any[]> {
  return this.http.get<any[]>(API_URL, { 
    headers: this.authHeaders(),
    responseType: 'json'
  });
}

deleteCompagnie(id: number): Observable<any> {
  return this.http.delete(`${API_URL}/${id}`, { 
    headers: this.authHeaders(),
    responseType: 'text' // Le backend renvoie une chaîne
  });
}

  getImageAsBase64(imageUrl: string): Observable<string> {
  return new Observable(observer => {
    const img = new Image();
    img.crossOrigin = 'Anonymous';
    img.src = imageUrl;
    
    img.onload = () => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      canvas.width = img.width;
      canvas.height = img.height;
      ctx?.drawImage(img, 0, 0);
      
      try {
        const dataUrl = canvas.toDataURL('image/jpeg');
        observer.next(dataUrl);
        observer.complete();
      } catch (e) {
        observer.error('Erreur de conversion');
      }
    };
    
    img.onerror = () => {
      observer.error('Erreur de chargement image');
    };
  });
}

}
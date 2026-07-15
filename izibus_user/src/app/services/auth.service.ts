import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

const API_URL = 'http://localhost:8080'; // Remplace par ton URL backend

interface JwtPayload {
  sub: string;
  compagnieId: number;
  adminId?: number;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  // Connexion Admin
  loginAdmin(credentials: { email: string, password: string }): Observable<any> {
    return this.http.post(`${API_URL}/api/v1/login/admin`, credentials);
  }

  getCurrentAdminId(): number | null {
    const token = localStorage.getItem('access_token');
    if (token) {
      try {
        const decoded = jwtDecode<JwtPayload>(token);
        return decoded.adminId || null;
      } catch (error) {
        console.error('Erreur de décodage JWT:', error);
        return null;
      }
    }
    return null;
  }

  getUserRole(): string | null {
    const token = localStorage.getItem('access_token');
    if (token) {
      try {
        const decoded = jwtDecode<JwtPayload>(token);
        return decoded.role || null;
      } catch (error) {
        console.error('Erreur de décodage JWT:', error);
        return null;
      }
    }
    return null;
  }

  // Inscription Compagnie
  registerCompany(companyData: any): Observable<any> {
    return this.http.post(`${API_URL}/api/v1/register/compagnie`,companyData);
  }

  // Connexion Compagnie
  loginCompany(credentials: { email: string, password: string }): Observable<any> {
    return this.http.post(`${API_URL}/api/v1/login/compagnie`, credentials);
  }

  getCurrentCompanyId(): number | null {
    const token = localStorage.getItem('access_token');
    if (token) {
      try {
        const decoded = jwtDecode<JwtPayload>(token);
        return decoded.compagnieId; // Utiliser idCompagnie au lieu de id
      } catch (error) {
        console.error('Erreur de décodage JWT:', error);
        return null;
      }
    }
    return null;
  }

}

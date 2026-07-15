// src/app/services/client.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError, tap } from 'rxjs';

const API_URL = 'http://localhost:8080/api/v1/clients';

export interface ClientDto {
  id: number;
  nomClient: string;
  prenomClient: string;
  telephoneClient: string;
  verified: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  constructor(private http: HttpClient) { }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token') || '';
    console.log('Using token for request:', token);
    return new HttpHeaders({ 
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getAllClients(): Observable<ClientDto[]> {
    const headers = this.authHeaders();
    console.log('Sending request to:', API_URL, 'with headers:', headers);
    
    return this.http.get<ClientDto[]>(API_URL, { headers }).pipe(
      tap(response => console.log('Received clients response:', response)),
      catchError(this.handleError)
    );
  }

  getGlobalClientStats(): Observable<any> {
  const url = `${API_URL}/global-stats`; // Doit correspondre à @GetMapping("/global-stats")
  return this.http.get(url, { headers: this.authHeaders() })
    .pipe(catchError(this.handleError));
}

  deleteClient(id: number): Observable<void> {
    const url = `${API_URL}/${id}`;
    console.log('Deleting client at URL:', url);
    
    return this.http.delete<void>(url, { 
      headers: this.authHeaders() 
    }).pipe(
      tap(() => console.log(`Client ${id} deleted successfully`)),
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'Une erreur inconnue est survenue';
    
    if (error.error instanceof ErrorEvent) {
        // Erreur côté client
        errorMessage = `Erreur réseau : ${error.error.message}`;
    } else if (error.status === 0) {
        // Serveur inaccessible
        errorMessage = 'Le serveur est inaccessible. Veuillez vérifier votre connexion ou contacter le support technique.';
    } else if (error.status === 404) {
        // Ressource non trouvée
        errorMessage = 'Aucun client trouvé. La liste des clients est vide.';
    } else if (error.status === 403) {
        // Accès refusé
        errorMessage = 'Accès refusé : vous ne disposez pas des permissions nécessaires.';
    } else {
        // Erreur serveur générale
        errorMessage = `Erreur serveur (${error.status}) : ${error.message}`;
    }
    
    console.error('API Error:', error);
    return throwError(() => new Error(errorMessage));
}

}
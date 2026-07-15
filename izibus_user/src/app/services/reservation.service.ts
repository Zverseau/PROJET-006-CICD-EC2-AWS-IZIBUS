// src/app/services/reservation.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/v1/reservations';

export interface ReservationDto {
  id: number;
  trajetId: number;
  compagnieId?: number;
  nomPassager: string;
  prenomPassager: string;
  telephonePassager: string;
  nombrePlacesReservees: number;
  montantTotal: number;
  annulee: boolean;
  
  // Ajouter les propriétés manquantes
  nomCompagnie?: string;
  lieuDepart: string;
  lieuArriver: string;
  dateTrajet: string;
  dateReservation: string;
  
  // Propriétés pour les clients
  nomClient?: string;
  prenomClient?: string;
  telephoneClient?: string;
}

export interface ReservationStatDto {
  today: number;
  week: number;
  month: number;
  year: number;
  cancelledToday: number;
}

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
export class ReservationService {

  constructor(private http: HttpClient) { }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('access_token') || '';
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  /**
   * La compagnie connectée réserve pour un passager non-enregistré.
   */
  reserverPourClient(
    compagnieId: number,
    payload: {
      trajetId: number;
      nomPassager: string;
      prenomPassager: string;
      telephonePassager: string;
      nombrePlacesReservees: number;
    }
  ): Observable<ReservationDto> {
    return this.http.post<ReservationDto>(
      `${API_URL}/compagnie/${compagnieId}/compagnie-reserve`,
      payload,
      { headers: this.authHeaders().set('Content-Type','application/json') }
    );
  }

  /** Récupère la liste des réservations de la compagnie connectée */
  getReservationsByCompagnie(compagnieId: number): Observable<ReservationDto[]> {
    return this.http.get<ReservationDto[]>(
      `${API_URL}/compagnie/${compagnieId}/mes-reservations`,
      { headers: this.authHeaders() }
    );
  }

  // Ajouter cette méthode
  genererTicketPdf(reservationId: number): Observable<Blob> {
    const headers = this.authHeaders().set('Accept', 'application/pdf');
    return this.http.get(`${API_URL}/${reservationId}/ticket`, {
      headers: headers,
      responseType: 'blob' // Corriger le type de réponse
    });
  }

  /** Récupère les réservations faites par des vrais clients */
  getClientReservationsByCompagnie(compagnieId: number): Observable<ReservationDto[]> {
    return this.http.get<ReservationDto[]>(
        `${API_URL}/compagnie/${compagnieId}/reservations-clients`,
      { headers: this.authHeaders() }
    );
  }

  getStatsByCompagnie(compagnieId: number): Observable<ReservationStatDto> {
  return this.http.get<ReservationStatDto>(
    `${API_URL}/compagnie/${compagnieId}/statistiques`,
    { headers: this.authHeaders() }
  );
}


// Ajoutez cette méthode
getGlobalStats(): Observable<ReservationStatDto> {
  return this.http.get<ReservationStatDto>(
    `${API_URL}/statistiques/global`,
    { headers: this.authHeaders() }
  );
}

  /** Récupère une réservation par son ID */
  getReservationById(id: number): Observable<ReservationDto> {
    return this.http.get<ReservationDto>(
      `${API_URL}/${id}`,
      { headers: this.authHeaders() }
    );
  }

  recupererReservationsParClient(clientId: number): Observable<ReservationDto[]> {
  return this.http.get<ReservationDto[]>(
    `${API_URL}/client/${clientId}`,
    { headers: this.authHeaders() }
  );
}

  /** Modifie une réservation existante */
  updateReservation(
    compagnieId: number,
    reservationId: number,
    payload: {
      trajetId: number;
      nomPassager: string;
      prenomPassager: string;
      telephonePassager: string;
      nombrePlacesReservees: number;
    }
  ): Observable<ReservationDto> {
    return this.http.put<ReservationDto>(
      `${API_URL}/compagnie/${compagnieId}/modifier/${reservationId}`,
      payload,
      {
        headers: this.authHeaders()
          .set('Content-Type', 'application/json')
      }
    );
  }

  /** Supprime une réservation */
  deleteReservation(id: number): Observable<void> {
    return this.http.delete<void>(
      `${API_URL}/supprimer/${id}`,
      { headers: this.authHeaders() }
    );
  }

  /** Récupère la liste des clients existants ayant au moins une réservation pour la compagnie */
  getClientsByCompagnie(compagnieId: number): Observable<ClientDto[]> {
    return this.http.get<ClientDto[]>(
      `${API_URL}/compagnie/${compagnieId}/clients`,
      { headers: this.authHeaders() }
    );
  }

  
}

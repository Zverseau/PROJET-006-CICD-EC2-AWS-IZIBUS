import { Component, OnInit } from '@angular/core';
import { ReservationService } from '../../../services/reservation.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-liste-reservation-clients',
  templateUrl: './liste-reservation-clients.component.html',
  styleUrls: ['./liste-reservation-clients.component.css']
})
export class ListeReservationClientsComponent implements OnInit {
  reservations: any[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const compId = this.authService.getCurrentCompanyId()!;
    this.reservationService.getClientReservationsByCompagnie(compId).subscribe({
      next: data => {
        this.reservations = data;
        this.isLoading = false;
      },
      error: err => {
        console.error('Erreur chargement réservations clients', err);
        this.errorMessage = 'Impossible de charger les réservations';
        this.isLoading = false;
      }
    });
  }
}
import { Component, OnInit } from '@angular/core';
import { ReservationService, ClientDto } from '../../../services/reservation.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-liste-client',
  templateUrl: './liste-client.component.html',
  styleUrls: ['./liste-client.component.css']
})
export class ListeClientComponent implements OnInit {
  clients: ClientDto[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const compagnieId = this.authService.getCurrentCompanyId();
    if (!compagnieId) {
      this.errorMessage = 'Compagnie non identifiée.';
      this.isLoading = false;
      return;
    }

    this.reservationService.getClientsByCompagnie(compagnieId).subscribe({
      next: data => {
        this.clients = data;
        this.isLoading = false;
      },
      error: err => {
        console.error('Erreur chargement clients :', err);
        this.errorMessage = err.error?.message || 'Erreur lors du chargement des clients.';
        this.isLoading = false;
      }
    });
  }
}

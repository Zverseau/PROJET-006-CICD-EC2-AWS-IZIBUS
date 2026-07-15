import { Component, OnInit } from '@angular/core';
import { ReservationService } from '../../../services/reservation.service';
import { AuthService } from '../../../services/auth.service';
import { saveAs } from 'file-saver';

// Ajouter l'interface ReservationDto
export interface ReservationDto {
  id: number;
  nomPassager: string;
  prenomPassager: string;
  telephonePassager: string;
  lieuDepart: string;
  lieuArriver: string;
  dateTrajet: string;
  nombrePlacesReservees: number;
  montantTotal: number;
  dateReservation: string;
}

@Component({
  selector: 'app-liste-reservation',
  templateUrl: './listereservation.component.html',
  styleUrls: ['./listereservation.component.css']
})
export class ListereservationComponent implements OnInit {
  reservations: ReservationDto[] = []; // Changer Reservation → ReservationDto
  loading = false;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations() {
    this.loading = true;
    const compagnieId = this.authService.getCurrentCompanyId()!;
    
    this.reservationService.getReservationsByCompagnie(compagnieId)
      .subscribe({
        next: (data) => {
          this.reservations = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Erreur de chargement', error);
          this.loading = false;
        }
      });
  }

  // Déplacer cette méthode AVANT supprimerReservation
  genererTicket(reservationId: number) {
    this.reservationService.genererTicketPdf(reservationId).subscribe(
      (blob) => {
        const filename = `ticket_reservation_${reservationId}.pdf`;
        saveAs(blob, filename);
      },
      (error) => {
        console.error('Erreur lors de la génération du ticket', error);
        alert('Erreur lors de la génération du ticket');
      }
    );
  }

  supprimerReservation(id: number) {
    if (!confirm('Voulez-vous vraiment supprimer cette réservation ?')) return;
    
    this.reservationService.deleteReservation(id).subscribe({
      next: () => {
        this.loadReservations();
      },
      error: (err) => {
        console.error('Erreur de suppression', err);
        alert('Échec de la suppression : ' + (err.error?.message || 'Erreur serveur'));
      }
    });
  }
}
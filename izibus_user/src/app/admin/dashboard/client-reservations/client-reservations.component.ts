import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReservationService, ReservationDto } from './../../../services/reservation.service';

@Component({
  selector: 'app-client-reservations',
  templateUrl: './client-reservations.component.html',
  styleUrls: ['./client-reservations.component.css']
})
export class ClientReservationsComponent implements OnInit {
  reservations: ReservationDto[] = [];
  loading = true;
  clientId!: number;
  clientName: string = '';

  constructor(
    private route: ActivatedRoute,
    private reservationService: ReservationService
  ) { }

  ngOnInit(): void {
    this.clientId = +this.route.snapshot.paramMap.get('id')!;
    this.loadReservations();
  }

  loadReservations(): void {
    this.reservationService.recupererReservationsParClient(this.clientId).subscribe({
      next: (reservations) => {
        this.reservations = reservations;
        
        // Utiliser les propriétés correctes du premier client
        if (reservations.length > 0) {
          this.clientName = `${reservations[0].nomClient || ''} ${reservations[0].prenomClient || ''}`;
        }
        
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des réservations', err);
        this.loading = false;
      }
    });
  }
}
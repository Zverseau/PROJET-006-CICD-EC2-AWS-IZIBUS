import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReservationService } from './../../../services/reservation.service';
import { CompagnieService } from './../../../services/compagnie.service';

@Component({
  selector: 'app-compagnie-reservations',
  templateUrl: './compagnie-reservations.component.html',
  styleUrls: ['./compagnie-reservations.component.css']
})
export class CompagnieReservationsComponent implements OnInit {
  compagnieId!: number;
  reservations: any[] = [];
  loading = true;
  compagnieName = '';
  compagnieLoading = true; // Nouveau état de chargement

  constructor(
    private route: ActivatedRoute,
    private reservationService: ReservationService,
    private compagnieService: CompagnieService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.compagnieId = +params['id'];
      this.loadCompagnieName();
      this.loadReservations();
    });
  }

  loadCompagnieName() {
    this.compagnieLoading = true;
    this.compagnieService.getCompagnieById(this.compagnieId).subscribe({
      next: (compagnie: any) => {
        this.compagnieName = compagnie.nomCompagnie;
        this.compagnieLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement de la compagnie', err);
        this.compagnieName = 'Compagnie inconnue';
        this.compagnieLoading = false;
      }
    });
  }

  loadReservations() {
    this.reservationService.getReservationsByCompagnie(this.compagnieId).subscribe({
      next: (data: any) => {
        this.reservations = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des réservations', err);
        this.loading = false;
      }
    });
  }
}
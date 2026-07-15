import { Component, OnInit } from '@angular/core';
import { ReservationService, ReservationStatDto } from '../../../services/reservation.service';
import { ClientService } from '../../../services/client.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-body1',
  templateUrl: './body.component.html',
  styleUrls: ['./body.component.css']
})
export class BodyComponent implements OnInit {
  isDarkMode = false;
  
  // Stats réservations
  reservationStats: ReservationStatDto = {
    today: 0,
    week: 0,
    month: 0,
    year: 0,
    cancelledToday: 0
  };

  // Stats clients
  clientStats: any = {
    totalClients: 0,
    today: 0,
    thisMonth: 0,
    thisYear: 0
  };

  loading = true;

  constructor(
    private reservationService: ReservationService,
    private clientService: ClientService
  ) {}

  ngOnInit(): void {
    // Récupération du thème sauvegardé
    const savedMode = localStorage.getItem('darkMode');
    if (savedMode) {
      this.isDarkMode = JSON.parse(savedMode);
    }
    
    this.loadStats();
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('darkMode', JSON.stringify(this.isDarkMode));
  }

  loadStats() {
    forkJoin({
      reservations: this.reservationService.getGlobalStats(),
      clients: this.clientService.getGlobalClientStats()
    }).subscribe({
      next: (results: any) => {
        this.reservationStats = results.reservations;
        this.clientStats = results.clients;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur stats:', err);
        this.loading = false;
      }
    });
  }
}
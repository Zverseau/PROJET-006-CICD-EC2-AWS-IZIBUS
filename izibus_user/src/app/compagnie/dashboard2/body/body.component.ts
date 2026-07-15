import { Component, OnInit } from '@angular/core';
import { ReservationService, ReservationStatDto } from '../../../services/reservation.service';
import { AuthService } from '../../../services/auth.service';
import { CompagnieService } from '../../../services/compagnie.service';
import { PaiementService } from '../../../services/paiement.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-body',
  templateUrl: './body.component.html',
  styleUrls: ['./body.component.css']
})
export class BodyComponent implements OnInit {
  isDarkMode = false;

  stats: ReservationStatDto = { 
    today: 0, 
    week: 0, 
    month: 0, 
    year: 0, 
    cancelledToday: 0 
  };

  caStats: any = {
    aujourdhui: 0,
    cetteSemaine: 0,
    ceMois: 0,
    cetteAnnee: 0
  };

  compagnie: any = {
    nomCompagnie: '',
    email: '',
    telephoneCompagnie: '',
    description: '',
    logoCompagnie: null
  };

  isLoading = true;
  errorMessage: string | null = null;
  currentDate = new Date();
  currentWeek: number;

  constructor(
    private authService: AuthService,
    private reservationService: ReservationService,
    private compagnieService: CompagnieService,
    private paiementService: PaiementService
  ) {
    this.currentWeek = this.getWeekNumber(this.currentDate);
  }

  toggleTheme() {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('darkMode', JSON.stringify(this.isDarkMode));
  }

  ngOnInit(): void {
    const savedMode = localStorage.getItem('darkMode');
    if (savedMode) {
      this.isDarkMode = JSON.parse(savedMode);
    }
    
    const compagnieId = this.authService.getCurrentCompanyId();
    
    if (compagnieId) {
      forkJoin([
        this.reservationService.getStatsByCompagnie(compagnieId)
          .pipe(catchError(err => {
            console.error('Erreur stats:', err);
            this.errorMessage = 'Erreur lors du chargement des statistiques';
            return of(this.stats);
          })),
        
        this.compagnieService.getCompagnieById(compagnieId)
          .pipe(catchError(err => {
            console.error('Erreur compagnie:', err);
            this.errorMessage = 'Erreur lors du chargement des informations';
            return of(this.compagnie);
          })),
        
        this.paiementService.getChiffreAffaireStatsByCompagnie(compagnieId)
          .pipe(catchError(err => {
            console.error('Erreur CA:', err);
            this.errorMessage = 'Erreur lors du chargement du chiffre d\'affaires';
            return of(this.caStats);
          }))
      ]).subscribe(([stats, compagnie, caStats]) => {
        this.stats = stats;
        this.compagnie = compagnie;
        this.caStats = caStats;
        this.isLoading = false;
      });
    } else {
      this.errorMessage = 'Aucune compagnie connectée. Veuillez vous connecter.';
      this.isLoading = false;
    }
  }

  getWeekNumber(date: Date): number {
    const firstDayOfYear = new Date(date.getFullYear(), 0, 1);
    const pastDaysOfYear = (date.getTime() - firstDayOfYear.getTime()) / 86400000;
    return Math.ceil((pastDaysOfYear + firstDayOfYear.getDay() + 1) / 7);
  }
  
}
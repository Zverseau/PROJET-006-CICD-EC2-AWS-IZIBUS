// src/app/compagnie/dashboard2/reserver/reserver.component.ts
import { Component, OnInit } from '@angular/core';
import { ReservationService } from '../../../services/reservation.service';
import { TrajetService } from '../../../services/trajet.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

interface Trajet {
  id: number;              // note : correspond à trajetId côté backend
  lieuDepart: string;
  lieuArriver: string;
  date: string | Date;
  heureDepart: string;
  heureArriver: string;
  nombrePlaces: number;
  prix: number;
}

@Component({
  selector: 'app-reserver',
  templateUrl: './reserver.component.html',
  styleUrls: ['./reserver.component.css']
})
export class ReserverComponent implements OnInit {
  // Pour le passager
  nomPassager = '';
  prenomPassager = '';
  telephonePassager = '';

  // Trajets & sélection
  trajets: Trajet[] = [];
  selectedTrajet: Trajet | null = null;

  // Nombre de places + calculs
  nombrePlacesReservees = 1;
  placesDisponibles = 0;
  prixUnitaire = 0;
  montantTotal = 0;

  // UI
  isLoading = false;
  errorMessage = '';

  constructor(
    private trajetService: TrajetService,
    private reservationService: ReservationService,
    private authService: AuthService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.loadTrajets();
  }

  private loadTrajets(): void {
    const compagnieId = this.authService.getCurrentCompanyId();
    if (!compagnieId) { return; }

    this.trajetService.getTrajetsByCompagnie(compagnieId).subscribe({
      next: (data: Trajet[]) => {
        this.trajets = data.map(t => ({
          ...t,
          date: new Date(t.date)
        }));
      },
      error: err => console.error('Erreur chargement trajets :', err)
    });
  }

  onTrajetSelect(): void {
    if (!this.selectedTrajet) { return; }
    this.placesDisponibles = this.selectedTrajet.nombrePlaces;
    this.prixUnitaire = this.selectedTrajet.prix;
    this.nombrePlacesReservees = 1;
    this.calculerMontant();
  }

  calculerMontant(): void {
    this.montantTotal = this.prixUnitaire * this.nombrePlacesReservees;
  }

  isFormValide(): boolean {
    return !!this.selectedTrajet
      && this.nomPassager.trim() !== ''
      && this.prenomPassager.trim() !== ''
      && this.telephonePassager.trim() !== ''
      && this.nombrePlacesReservees >= 1
      && this.nombrePlacesReservees <= this.placesDisponibles;
  }

  reserver(): void {
    if (!this.isFormValide()) { return; }
    this.isLoading = true;
    this.errorMessage = '';

    const compagnieId = this.authService.getCurrentCompanyId()!;
    const payload = {
      trajetId: this.selectedTrajet!.id,
      nomPassager: this.nomPassager,
      prenomPassager: this.prenomPassager,
      telephonePassager: this.telephonePassager,
      nombrePlacesReservees: this.nombrePlacesReservees
    };

    this.reservationService
      .reserverPourClient(compagnieId, payload)
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/dash2/listeReservation']);
        },
        error: err => {
          this.isLoading = false;
          this.errorMessage = err.error?.message || 'Erreur lors de la réservation';
        }
      });
  }
}

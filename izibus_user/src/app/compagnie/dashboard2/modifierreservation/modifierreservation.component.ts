import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ReservationService, ReservationDto } from '../../../services/reservation.service';
import { TrajetService } from '../../../services/trajet.service';
import { AuthService } from '../../../services/auth.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-modifierreservation',
  templateUrl: './modifierreservation.component.html',
  styleUrls: ['./modifierreservation.component.css']
})
export class ModifierreservationComponent implements OnInit {
  id!: number;

  // FORM FIELDS
  nomPassager = '';
  prenomPassager = '';
  telephonePassager = '';
  selectedTrajetId!: number;
  nombrePlacesReservees = 1;

  // CALCUL
  trajetsDisponibles: any[] = [];
  placesDisponibles = 0;
  prixUnitaire = 0;
  montantTotal = 0;

  isLoading = false;
  errorMessage = '';
  initialLoadComplete = false; // Nouveau flag pour suivre le chargement initial

  constructor(
    private route: ActivatedRoute,
    private reservationService: ReservationService,
    private trajetService: TrajetService,
    private authService: AuthService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.id = +this.route.snapshot.params['id'];

    console.log('ID Réservation:', this.id);
    const compId = this.authService.getCurrentCompanyId()!;
    console.log('ID Compagnie:', compId);

    // 1. Charger simultanément la réservation et les trajets disponibles
    forkJoin([
      this.reservationService.getReservationById(this.id),
      this.trajetService.getTrajetsByCompagnie(compId)
    ]).subscribe({
      next: ([reservation, trajets]) => {
        console.log('Réservation chargée:', reservation);
        console.log('Trajets disponibles:', trajets);

        // Stocker les trajets disponibles
        this.trajetsDisponibles = trajets;
        

        // Pré-remplir les champs du formulaire
        this.nomPassager = reservation.nomPassager;
        this.prenomPassager = reservation.prenomPassager;
        this.telephonePassager = reservation.telephonePassager;
        this.selectedTrajetId = reservation.trajetId;
        console.log('Trajet ID sélectionné:', this.selectedTrajetId);
        this.nombrePlacesReservees = reservation.nombrePlacesReservees;

        // 2. Charger les détails du trajet sélectionné
        this.trajetService.getTrajetById(reservation.trajetId).subscribe({
          next: t => {
            this.prixUnitaire = t.prix;
            this.placesDisponibles = t.nombrePlaces + this.nombrePlacesReservees;
            this.recalculerMontant();
            
            // Marquer le chargement initial comme terminé
            this.initialLoadComplete = true;
          },
          error: err => {
            console.error('Erreur chargement trajet', err);
            this.errorMessage = 'Erreur lors du chargement du trajet lié';
          }
        });
      },
      error: err => {
        console.error('Erreur forkJoin:', err);
        this.errorMessage = 'Impossible de charger les données nécessaires';
      }
    });
  }

  onTrajetChange(): void {
    if (!this.selectedTrajetId) {
      this.prixUnitaire = 0;
      this.placesDisponibles = 0;
      this.recalculerMontant();
      return;
    }
    
    // Charger les détails du nouveau trajet sélectionné
    this.trajetService.getTrajetById(this.selectedTrajetId).subscribe({
      next: t => {
        this.prixUnitaire = t.prix;
        this.placesDisponibles = t.nombrePlaces;
        this.recalculerMontant();
      },
      error: err => {
        console.error('Erreur chargement trajet sélectionné', err);
        this.prixUnitaire = 0;
        this.placesDisponibles = 0;
        this.recalculerMontant();
      }
    });
  }

  recalculerMontant(): void {
    this.montantTotal = this.prixUnitaire * this.nombrePlacesReservees;
  }

  onSubmit(): void {
    if (!confirm('Confirmer la modification ?')) return;
    
    this.isLoading = true;
    const compId = this.authService.getCurrentCompanyId()!;
    const dto = {
      trajetId: this.selectedTrajetId,
      nomPassager: this.nomPassager,
      prenomPassager: this.prenomPassager,
      telephonePassager: this.telephonePassager,
      nombrePlacesReservees: this.nombrePlacesReservees
    };
    
    this.reservationService.updateReservation(compId, this.id, dto).subscribe({
      next: () => {
        alert('✅ Réservation modifiée avec succès !');
        this.router.navigate(['/dash2','listeReservation']);
      },
      error: err => {
        this.isLoading = false;
        console.error('Erreur modif :', err);
        alert(err.error?.message || 'Échec de la modification');
      }
    });
  }
  
}
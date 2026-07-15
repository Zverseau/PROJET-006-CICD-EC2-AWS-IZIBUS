import { Component, OnInit } from '@angular/core';
import { CompagnieService } from './../../../services/compagnie.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-compagnies-list',
  templateUrl: './compagnies-list.component.html',
  styleUrls: ['./compagnies-list.component.css']
})
export class CompagniesListComponent implements OnInit {
  compagnies: any[] = [];
  loading = true;
  errorMessage: string | null = null;
  serverUnavailable = false;
  searchTerm: string = '';

  constructor(
    private compagnieService: CompagnieService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCompagnies();
  }

  // Filtrage côté client
  get filteredCompagnies(): any[] {
    if (!this.searchTerm) return this.compagnies;
    
    const term = this.searchTerm.toLowerCase();
    return this.compagnies.filter(comp => 
      comp.nomCompagnie.toLowerCase().includes(term) || 
      comp.email.toLowerCase().includes(term)
    );
  }

  loadCompagnies() {
    this.errorMessage = null;
    this.serverUnavailable = false;
    this.loading = true;
    this.searchTerm = ''; // Réinitialiser la recherche
    
    this.compagnieService.getAllCompagnies().subscribe({
      next: (data) => {
        this.compagnies = data;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        console.error('Erreur lors du chargement des compagnies', err);
        this.loading = false;
        
        if (err.status === 0) {
          this.serverUnavailable = true;
          this.errorMessage = 'Impossible de se connecter au serveur';
        } else {
          this.errorMessage = err.message || 'Erreur lors du chargement des compagnies';
        }
      }
    });
  }

  // Méthode alternative si vous utilisez l'approche sans ngModel
  onSearchInput(event: any) {
    this.searchTerm = event.target.value;
  }

  viewTrajets(compagnieId: number) {
    this.router.navigate(['/dash1/compagnie', compagnieId, 'trajets']);
  }

  viewReservations(compagnieId: number) {
    this.router.navigate(['/dash1/compagnie', compagnieId, 'reservations']);
  }

  deleteCompagnie(compagnieId: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette compagnie ?')) {
      this.compagnieService.deleteCompagnie(compagnieId).subscribe({
        next: () => {
          this.compagnies = this.compagnies.filter(c => c.id !== compagnieId);
          alert('Compagnie supprimée avec succès');
        },
        error: (err) => {
          console.error('Erreur lors de la suppression', err);
          alert('Échec de la suppression : ' + 
                (err.status === 0 
                 ? 'Serveur inaccessible' 
                 : err.message || 'Erreur inconnue'));
        }
      });
    }
  }
}
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { TrajetService } from '../../../services/trajet.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-listetrajet',
  templateUrl: './listetrajet.component.html',
  styleUrls: ['./listetrajet.component.css']
})
export class ListetrajetComponent implements OnInit {
  trajets: any[] = [];
  isLoading: boolean = true; // Ajouté pour la gestion du chargement
  errorMessage: string = ''; // Ajouté pour la gestion des erreurs

  constructor(
    private trajetService: TrajetService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadTrajets();
  }

  loadTrajets() {
    const compagnieId = this.authService.getCurrentCompanyId();
    if (!compagnieId) {
      this.isLoading = false;
      this.errorMessage = "ID compagnie non trouvé. Veuillez vous reconnecter.";
      return;
    }

    this.trajetService.getTrajetsByCompagnie(compagnieId).subscribe({
      next: (data: any[]) => {
        this.trajets = data.map(t => ({
          ...t,
          heureDepart: this.formatTime(t.heureDepart),
          heureArriver: this.formatTime(t.heureArriver)
        }));
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: err => {
        console.error('Erreur chargement trajets :', err);
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Impossible de charger les trajets.';
        this.cdr.detectChanges();
      }
    });
  }

  private formatTime(timeString: string): string {
    if (!timeString) return 'N/A';
    const [h, m] = timeString.split(':');
    return `${h}:${m}`;
  }

  supprimerTrajet(id: number): void {
    if (!confirm('Confirmer la suppression ?')) return;

    const backup = [...this.trajets];
    this.trajets = this.trajets.filter(t => t.id !== id);
    this.cdr.detectChanges();

    this.trajetService.deleteTrajet(id).subscribe({
      next: (msg: string) => {
        alert(`✅ ${msg}`);
      },
      error: err => {
        console.error('Erreur suppression trajet :', err);
        this.trajets = backup;
        this.cdr.detectChanges();
        const msg = err.error?.message || 'Erreur lors de la suppression.';
        alert(`❌ Erreur : ${msg}`);
      }
    });
  }
}
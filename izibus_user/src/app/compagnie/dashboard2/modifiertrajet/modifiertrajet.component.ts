import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TrajetService } from '../../../services/trajet.service';

@Component({
  selector: 'app-modifiertrajet',
  templateUrl: './modifiertrajet.component.html',
  styleUrls: ['./modifiertrajet.component.css']
})
export class ModifiertrajetComponent implements OnInit {
  trajet: any = {};
  isSubmitting = false; // Ajout de l'état de soumission

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private trajetService: TrajetService
  ) { }

  ngOnInit(): void {
    const id = +this.route.snapshot.params['id'];
    this.trajetService.getTrajetById(id).subscribe({
      next: data => {
        this.trajet = {
          ...data,
          date: new Date(data.date).toISOString().split('T')[0],
          heureDepart: this.parseTime(data.heureDepart),
          heureArriver: this.parseTime(data.heureArriver)
        };
      },
      error: err => {
        console.error('Erreur chargement trajet :', err);
        const msg = err.error?.message || 'Impossible de charger le trajet.';
        alert(`❌ Erreur : ${msg}`);
      }
    });
  }

  private parseTime(timeString: string): string {
    const [h, m] = timeString.split(':');
    return `${h.padStart(2,'0')}:${m.padStart(2,'0')}`;
  }

  onSubmit(): void {
    this.isSubmitting = true; // Activer l'état de soumission
    
    this.trajetService.updateTrajet(this.trajet.id, this.trajet)
      .subscribe({
        next: () => {
          this.isSubmitting = false; // Désactiver l'état de soumission
          alert('✅ Trajet modifié avec succès !');
          this.router.navigate(['/dash2/listeTrajet']);
        },
        error: err => {
          this.isSubmitting = false; // Désactiver l'état de soumission
          console.error('Erreur modification trajet :', err);
          const msg = err.error?.message || 'Erreur inconnue lors de la modification.';
          alert(`❌ Erreur : ${msg}`);
        }
      });
  }
}
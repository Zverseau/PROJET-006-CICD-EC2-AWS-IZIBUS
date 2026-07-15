import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TrajetService } from '../../../services/trajet.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ajoutertrajet',
  templateUrl: './ajoutertrajet.component.html',
  styleUrls: ['./ajoutertrajet.component.css']
})
export class AjoutertrajetComponent {
  trajetForm: FormGroup;
  isSubmitting = false;
  activeFields: { [key: string]: boolean } = {};

  constructor(
    private fb: FormBuilder,
    private trajetService: TrajetService,
    private authService: AuthService,
    private router: Router
  ) {
    this.trajetForm = this.fb.group({
      lieuDepart:   ['', Validators.required],
      lieuArriver:  ['', Validators.required],
      heureDepart:  ['', Validators.required],
      heureArriver: ['', Validators.required],
      date:         ['', Validators.required],
      prix:         ['', [Validators.required, Validators.min(0)]],
      nombrePlaces: ['', [Validators.required, Validators.min(1)]]
    });
  }

  // Activation des labels flottants
  activateLabel(field: string) {
    this.activeFields[field] = true;
  }

  // Désactivation conditionnelle des labels
  deactivateLabel(field: string) {
    if (!this.trajetForm.get(field)?.value) {
      this.activeFields[field] = false;
    }
  }

  // Vérifie si un champ est actif
  isFieldActive(field: string): boolean {
    return !!this.activeFields[field];
  }

  // Réinitialisation du formulaire
  resetForm() {
    this.trajetForm.reset();
    this.activeFields = {};
  }

  onSubmit() {
    if (!this.trajetForm.valid) {
      alert('Veuillez remplir tous les champs correctement.');
      return;
    }

    const compagnieId = this.authService.getCurrentCompanyId();
    if (!compagnieId) {
      alert('Compagnie non identifiée !');
      return;
    }

    this.isSubmitting = true;
    
    const trajetData = {
      ...this.trajetForm.value,
      compagnieId: compagnieId
    };

    this.trajetService.createTrajet(trajetData).subscribe({
      next: () => {
        this.isSubmitting = false;
        alert('✅ Trajet créé avec succès !');
        this.router.navigate(['/dash2/listeTrajet']);
      },
      error: err => {
        this.isSubmitting = false;
        console.error('Erreur création trajet :', err);
        const msg = err.error?.message || 'Erreur inconnue lors de la création du trajet.';
        alert(`❌ Erreur : ${msg}`);
      }
    });
  }
}
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TrajetService } from './../../../services/trajet.service';
import { CompagnieService } from './../../../services/compagnie.service';

@Component({
  selector: 'app-compagnie-trajets',
  templateUrl: './compagnie-trajets.component.html',
  styleUrls: ['./compagnie-trajets.component.css']
})
export class CompagnieTrajetsComponent implements OnInit {
  compagnieId!: number;
  trajets: any[] = [];
  loading = true;
  compagnieName = '';
  compagnieLoading = true; // Nouveau état de chargement

  constructor(
    private route: ActivatedRoute,
    private trajetService: TrajetService,
    private compagnieService: CompagnieService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.compagnieId = +params['id'];
      this.loadCompagnieName();
      this.loadTrajets();
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

  loadTrajets() {
    this.trajetService.getTrajetsByCompagnie(this.compagnieId).subscribe({
      next: (data: any) => {
        this.trajets = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des trajets', err);
        this.loading = false;
      }
    });
  }
}
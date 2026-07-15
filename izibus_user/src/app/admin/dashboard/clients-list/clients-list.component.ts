import { Component, OnInit } from '@angular/core';
import { ClientService, ClientDto } from './../../../services/client.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-clients-list',
  templateUrl: './clients-list.component.html',
  styleUrls: ['./clients-list.component.css']
})
export class ClientsListComponent implements OnInit {
  clients: ClientDto[] = [];
  loading = true;
  errorMessage: string | null = null;
  serverUnavailable = false;
  searchTerm: string = '';

  constructor(private clientService: ClientService) { }

  ngOnInit(): void {
    this.loadClients();
  }

  // Filtrage côté client - getter uniquement (pas de déclaration séparée)
  get filteredClients(): ClientDto[] {
    if (!this.searchTerm) return this.clients;
    
    const term = this.searchTerm.toLowerCase();
    return this.clients.filter(client => 
      (client.nomClient?.toLowerCase().includes(term) || 
      (client.prenomClient?.toLowerCase().includes(term)) || 
      (client.telephoneClient?.includes(term))
    )
    );
  }

  loadClients(): void {
    this.errorMessage = null;
    this.serverUnavailable = false;
    this.loading = true;
    this.searchTerm = ''; // Réinitialiser la recherche
    
    this.clientService.getAllClients().subscribe({
      next: (clients) => {
        this.clients = clients;
        this.loading = false;
      },
      error: (err: HttpErrorResponse) => {
        console.error('Erreur lors du chargement des clients', err);
        this.loading = false;
        
        if (err.status === 0) {
          this.serverUnavailable = true;
          this.errorMessage = 'Impossible de se connecter au serveur';
        } else {
          this.errorMessage = err.message || 'Erreur lors du chargement des clients';
        }
      }
    });
  }

  deleteClient(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {
      this.clientService.deleteClient(id).subscribe({
        next: () => {
          this.clients = this.clients.filter(client => client.id !== id);
        },
        error: (err) => {
          console.error('Erreur lors de la suppression', err);
          alert('Échec de la suppression : ' + (err.message || 'Erreur inconnue'));
        }
      });
    }
  }
}
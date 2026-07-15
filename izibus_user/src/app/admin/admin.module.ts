import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // Ajouté
import { HttpClientModule } from '@angular/common/http'; // Ajouté

import { AdminRoutingModule } from './admin-routing.module';
import { BodyComponent } from './dashboard/body/body.component';
import { SidebarComponent } from './dashboard/sidebar/sidebar.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CompagniesListComponent } from './dashboard/compagnies-list/compagnies-list.component';
import { CompagnieTrajetsComponent } from './dashboard/compagnie-trajets/compagnie-trajets.component';
import { CompagnieReservationsComponent } from './dashboard/compagnie-reservations/compagnie-reservations.component';
import { ClientsListComponent } from './dashboard/clients-list/clients-list.component';
import { ClientReservationsComponent } from './dashboard/client-reservations/client-reservations.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    BodyComponent,
    SidebarComponent,
    DashboardComponent,
    CompagniesListComponent,
    CompagnieTrajetsComponent,
    CompagnieReservationsComponent,
    ClientsListComponent,
    ClientReservationsComponent
  ],
  imports: [
    CommonModule,
    RouterModule, // Ajouté
    HttpClientModule, // Ajouté
    AdminRoutingModule,
    FormsModule
  ]
})
export class AdminModule { }
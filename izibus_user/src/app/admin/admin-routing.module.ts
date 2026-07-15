// admin-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { BodyComponent } from './dashboard/body/body.component';
import { CompagniesListComponent } from './dashboard/compagnies-list/compagnies-list.component';
import { CompagnieTrajetsComponent } from './dashboard/compagnie-trajets/compagnie-trajets.component';
import { CompagnieReservationsComponent } from './dashboard/compagnie-reservations/compagnie-reservations.component';
import { ClientsListComponent } from './dashboard/clients-list/clients-list.component';
import { ClientReservationsComponent } from './dashboard/client-reservations/client-reservations.component';

const routes: Routes = [
  {
    path: 'dash1', 
    component: DashboardComponent,
    children: [
      { path: 'dashboard1', component: BodyComponent },
      { path: 'compagnies', component: CompagniesListComponent },
      { path: 'compagnie/:id/trajets', component: CompagnieTrajetsComponent },
      { path: 'compagnie/:id/reservations', component: CompagnieReservationsComponent },
      { path: 'clients', component: ClientsListComponent },
      { path: 'client/:id/reservations', component: ClientReservationsComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
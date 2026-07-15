import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Dashboard2Component } from './dashboard2/dashboard2.component';
import { BodyComponent } from './dashboard2/body/body.component';
import {ListeClientComponent} from './dashboard2/liste-client/liste-client.component';
import { AjoutertrajetComponent } from './dashboard2/ajoutertrajet/ajoutertrajet.component';
import { ModifiertrajetComponent } from './dashboard2/modifiertrajet/modifiertrajet.component';
import { ListetrajetComponent } from './dashboard2/listetrajet/listetrajet.component';
import { ReserverComponent } from './dashboard2/reserver/reserver.component';
import { ListereservationComponent } from './dashboard2/listereservation/listereservation.component';
import { ModifierreservationComponent } from './dashboard2/modifierreservation/modifierreservation.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ListeReservationClientsComponent } from './dashboard2/liste-reservation-clients/liste-reservation-clients.component';

const routes: Routes = [
  {path: 'dash2', component:Dashboard2Component,
    children:[
      { path: 'dashboard2', component: BodyComponent },
      { path: 'listeClient', component: ListeClientComponent },
      {path: 'ajouterTrajet', component:AjoutertrajetComponent},
      { path: 'modifierTrajet/:id', component: ModifiertrajetComponent }, // Route corrigée
      {path: 'listeTrajet', component:ListetrajetComponent},
      {path: 'reserver', component: ReserverComponent},
      {path: 'listeReservation', component: ListereservationComponent},
      {path: 'listeReservationClients', component: ListeReservationClientsComponent},
      {path: 'modifierReservation/:id', component: ModifierreservationComponent}
    ]
  }
];
@NgModule({
  imports: [
    RouterModule.forChild(routes),
     ReactiveFormsModule
  ],
  exports: [RouterModule]
})
export class CompagnieRoutingModule { }

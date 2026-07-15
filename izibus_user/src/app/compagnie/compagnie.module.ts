import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { CompagnieRoutingModule } from './compagnie-routing.module';
import { Dashboard2Component } from './dashboard2/dashboard2.component';
import { BodyComponent } from './dashboard2/body/body.component';
import { SidebarComponent } from './dashboard2/sidebar/sidebar.component';
import { ListeClientComponent } from './dashboard2/liste-client/liste-client.component';
import { AjoutertrajetComponent } from './dashboard2/ajoutertrajet/ajoutertrajet.component';
import { ModifiertrajetComponent } from './dashboard2/modifiertrajet/modifiertrajet.component';
import { ListetrajetComponent } from './dashboard2/listetrajet/listetrajet.component';
import { ReserverComponent } from './dashboard2/reserver/reserver.component';
import { ListereservationComponent } from './dashboard2/listereservation/listereservation.component';
import { ModifierreservationComponent } from './dashboard2/modifierreservation/modifierreservation.component';
import { ReactiveFormsModule } from '@angular/forms';
import { ListeReservationClientsComponent } from './dashboard2/liste-reservation-clients/liste-reservation-clients.component';


@NgModule({
  declarations: [
    Dashboard2Component,
    BodyComponent,
    SidebarComponent,
    ListeClientComponent,
    AjoutertrajetComponent,
    ModifiertrajetComponent,
    ListetrajetComponent,
    ReserverComponent,
    ListereservationComponent,
    ModifierreservationComponent,
    ListeReservationClientsComponent
  ],
  imports: [
    CommonModule,
    CompagnieRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule
  ]
})
export class CompagnieModule { }

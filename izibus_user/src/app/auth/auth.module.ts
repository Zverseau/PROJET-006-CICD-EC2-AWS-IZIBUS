import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthRoutingModule } from './auth-routing.module';
import { AuthentificationComponent } from './authentification/authentification.component';
import { ConnexionComponent } from './authentification/components/connexion/connexion.component';
import { InscriptionComponent } from './authentification/components/inscription/inscription.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http'; 


@NgModule({
  declarations: [
    AuthentificationComponent,
    ConnexionComponent,
    InscriptionComponent
  ],
  imports: [
    CommonModule,
    AuthRoutingModule,
    HttpClientModule,
    ReactiveFormsModule
  ]
})
export class AuthModule { }

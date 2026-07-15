import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-connexion',
  templateUrl: './connexion.component.html',
  styleUrls: ['./connexion.component.css']
})
export class ConnexionComponent {
  loginForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  // Attribut dans la classe
  showPassword: boolean = false;

  // Méthode toggle
  togglePasswordVisibility(): void {
  this.showPassword = !this.showPassword;
  }

  onSubmit() {
  if (this.loginForm.valid) {
    const credentials = this.loginForm.value;
    
    // Essayer d'abord la connexion admin
    this.authService.loginAdmin(credentials).subscribe({
      next: (adminRes: any) => {
        localStorage.setItem('access_token', adminRes.token);
        console.log('Token Admin:', localStorage.getItem('access_token'));
        
        // Vérifier le rôle
        const role = this.authService.getUserRole();
        console.log('Rôle détecté (Admin):', role);
        console.log('Tentative connexion admin...');
        
        if (role === 'ADMIN') {
          this.router.navigate(['/dash1/dashboard1']); // Espace admin
        } else {
          // Si pas admin, essayer la connexion compagnie
          this.tryCompanyLogin(credentials);
        }
      },
      error: (adminErr) => {
        // Si erreur admin, essayer la connexion compagnie
        console.log('Erreur admin, tentative compagnie...');
        this.tryCompanyLogin(credentials);
      }
    });
  }
}

// Nouvelle méthode pour gérer la connexion compagnie
tryCompanyLogin(credentials: any) {
  this.authService.loginCompany(credentials).subscribe({
    next: (compRes: any) => {
      localStorage.setItem('access_token', compRes.token);
      console.log('Token Compagnie:', localStorage.getItem('access_token'));
      
      const role = this.authService.getUserRole();
      console.log('Rôle détecté (Compagnie):', role);
      console.log('Tentative connexion admin...');
      
      if (role === 'COMPAGNIE') {
        this.router.navigate(['/dash2/dashboard2']); // Espace compagnie
      } else {
        alert('Rôle utilisateur non reconnu !');
      }
    },
    error: (compErr) => {
      console.error('Erreur compagnie:', compErr);
      alert('Identifiants incorrects !');
    }
  });
}

}
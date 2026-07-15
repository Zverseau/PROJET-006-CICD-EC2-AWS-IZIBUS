import { Component, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { HttpBackend, HttpClient } from '@angular/common/http';

// Remplacez par votre clé API ImgBB
const IMGBB_API_KEY = '1c653e6348aa49ed0db46f5ed69b1003';

@Component({
  selector: 'app-inscription',
  templateUrl: './inscription.component.html',
  styleUrls: ['./inscription.component.css']
})
export class InscriptionComponent {
  @ViewChild('fileInput') fileInput!: ElementRef;
  registerForm: FormGroup;
  logoPreview: string | null = null;
  uploadInProgress = false;
  private httpWithoutInterceptor: HttpClient;
  showPassword: boolean = false;
  showConfirmPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private handler: HttpBackend
  ) {
    this.httpWithoutInterceptor = new HttpClient(handler); // HttpClient sans intercepteur
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      nomCompagnie: ['', Validators.required],
      telephoneCompagnie: ['', [Validators.required, Validators.pattern(/^(\+?228|0)?\d{8}$/)]],
      description: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      logoCompagnie: ['', Validators.required]
    }, { 
      validators: this.passwordMatchValidator 
    });
  }

  passwordMatchValidator(form: FormGroup) {
    return form.get('password')?.value === form.get('confirmPassword')?.value 
      ? null : { mismatch: true };
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.uploadInProgress = true;
      const reader = new FileReader();
      reader.onload = () => this.logoPreview = reader.result as string;
      reader.readAsDataURL(file);
      this.uploadImageToImgBB(file);
    }
  }

  private uploadImageToImgBB(file: File) {
    const formData = new FormData();
    formData.append('image', file);

    this.httpWithoutInterceptor.post(`https://api.imgbb.com/1/upload?key=${IMGBB_API_KEY}`, formData)
      .subscribe({
        next: (res: any) => {
          this.uploadInProgress = false;
          if (res.data?.url) {
            this.registerForm.patchValue({ logoCompagnie: res.data.url });
          }
        },
        error: (err) => {
          this.uploadInProgress = false;
          console.error('Erreur ImgBB:', err);
          alert('Échec de l\'upload. Vérifiez la console pour les détails.');
        }
      });
  }

  private resetFileInput() {
    this.logoPreview = null;
    this.fileInput.nativeElement.value = '';
    this.registerForm.patchValue({ logoCompagnie: '' });
  }

  // Méthodes toggle
  togglePasswordVisibility(): void {
  this.showPassword = !this.showPassword;
    }

  toggleConfirmPasswordVisibility(): void {
  this.showConfirmPassword = !this.showConfirmPassword;
    }

  onSubmit() {
  if (this.registerForm.valid && !this.uploadInProgress) {
    console.log('Données envoyées:', this.registerForm.value);
    this.authService.registerCompany(this.registerForm.value).subscribe({
      next: (res: any) => {
        console.log('Réponse du backend:', res);
        alert(res.message);
        this.router.navigate(['/auth/connexion']);
      },
      error: (err) => {
        console.error('Erreur complète:', err);
        let errorMessage = 'Erreur inconnue';
        if (err.error?.error) {
          errorMessage = err.error.error;
        } else if (err.message) {
          errorMessage = err.message;
        }
        alert('Erreur: ' + errorMessage);
      }
    });
  }
}


}
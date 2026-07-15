import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Liste des endpoints qui ne nécessitent PAS de token
    const noAuthUrls = [
      '/register', 
      '/login',
      '/trajets',
      '/api/v1/clients',
    ];

    // Vérifier si la requête est pour une URL ne nécessitant pas d'authentification
    if (noAuthUrls.some(url => request.url.includes(url))) {
      return next.handle(request); // Pas de token
    }

    const token = localStorage.getItem('access_token');
    if (token) {
      request = request.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    return next.handle(request);
  }
}
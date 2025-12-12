import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = `${environment.apiUrl}/auth`;

  private loggedIn$ = new BehaviorSubject<boolean>(this.hasToken());

  constructor(private http: HttpClient, private router: Router) { }

  register(form: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, form);
  }


  login(credentials: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, credentials)
      .pipe(
        tap((response: any) => {
          const token = response?.accessToken || response?.token;
          if (token) {
            this.saveToken(token);

            if (response?.user) {
              this.saveUser(response.user);
            }

            this.loggedIn$.next(true);
          }
        })
      );
  }


  private saveToken(token: string) {
    localStorage.setItem('token', token);
  }


  private saveUser(user: any) {
    localStorage.setItem('user', JSON.stringify(user));
  }


  getToken(): string | null {
    return localStorage.getItem('token');
  }


  getUser(): any {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }


  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.loggedIn$.next(false);
    this.router.navigate(['/login']);
  }


  isLoggedIn(): boolean {
    return this.loggedIn$.value;
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('token');
  }


  loginStatus() {
    return this.loggedIn$.asObservable();
  }


  validateSession(): Observable<any> {
    return this.http.get(`${this.baseUrl}/me`);
  }

  verifyOtp(email: string, otp: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/verify-otp`, { email, otp });
  }

  resendOtp(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/resend-otp`, { email });
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/forgot-password`, { email });
  }

  resetPassword(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/reset-password`, data);
  }
}

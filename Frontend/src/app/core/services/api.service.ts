import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }


  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  }


  get<T>(path: string, params: HttpParams = new HttpParams()): Observable<T> {
    return this.http
      .get<T>(`${this.baseUrl}/${path}`, {
        headers: this.getHeaders(),
        params
      })
      .pipe(catchError(this.handleError));
  }


  post<T>(path: string, body: any): Observable<T> {
    return this.http
      .post<T>(`${this.baseUrl}/${path}`, body, {
        headers: this.getHeaders()
      })
      .pipe(catchError(this.handleError));
  }

  put<T>(path: string, body: any): Observable<T> {
    return this.http
      .put<T>(`${this.baseUrl}/${path}`, body, {
        headers: this.getHeaders()
      })
      .pipe(catchError(this.handleError));
  }

  delete<T>(path: string): Observable<T> {
    return this.http
      .delete<T>(`${this.baseUrl}/${path}`, {
        headers: this.getHeaders()
      })
      .pipe(catchError(this.handleError));
  }


  private handleError(error: any) {
    console.error('API Error:', error);

    let message = 'Something went wrong. Please try again.';

    if (error.status === 0) {
      message = 'Cannot connect to server. Please check backend.';
    }

    if (error.status === 401) {
      message = 'Unauthorized. Please login again.';
    }

    if (error.error) {
      if (typeof error.error === 'string') {
        message = error.error;
      } else if (error.error.message) {
        message = error.error.message;
      }
    }

    return throwError(() => new Error(message));
  }
}

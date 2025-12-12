import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) { }

  getAdminStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/stats`);
  }

  getAllRestaurants() {
    return this.http.get(`${this.apiUrl}/restaurants`);
  }

  approveRestaurant(id: number) {
    return this.http.put(`${this.apiUrl}/restaurants/${id}/approve`, {});
  }

  blockRestaurant(id: number) {
    return this.http.put(`${this.apiUrl}/restaurants/${id}/block`, {});
  }

  activateRestaurant(id: number) {
    return this.http.put(`${this.apiUrl}/restaurants/${id}/activate`, {});
  }

  getUsers() {
    return this.http.get(`${this.apiUrl}/users`);
  }

  blockUser(id: number) {
    return this.http.put(`${this.apiUrl}/users/${id}/block`, {});
  }

  activateUser(id: number) {
    return this.http.put(`${this.apiUrl}/users/${id}/activate`, {});
  }

  getAnalytics() {
    return this.http.get(`${this.apiUrl}/analytics`);
  }


  getCoupons() {
    return this.http.get(`${this.apiUrl}/coupons`);
  }

  addCoupon(coupon: any) {
    return this.http.post(`${this.apiUrl}/coupons`, coupon);
  }

  deleteCoupon(id: number) {
    return this.http.delete(`${this.apiUrl}/coupons/${id}`);
  }
}

import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { ApiService } from './api.service';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(
    private apiService: ApiService,
    private authService: AuthService
  ) { }


  getRestaurants(search?: string, location?: string, cuisine?: string) {
    let params = new HttpParams();
    if (search) params = params.set('search', search);
    if (location) params = params.set('location', location);
    if (cuisine) params = params.set('cuisine', cuisine);

    return this.apiService.get('restaurants', params);
  }


  getRestaurantDetail(id: number) {
    return this.apiService.get(`restaurants/${id}`);
  }


  getOrderHistory() {
    const user = this.authService.getUser();
    if (!user || !user.id) {

      return this.apiService.get('orders/customer/0');
    }
    return this.apiService.get(`orders/customer/${user.id}`);
  }


  getOrderTracking(orderId: number) {
    return this.apiService.get(`orders/${orderId}`);
  }


  placeOrder(order: any) {
    return this.apiService.post('orders', order);
  }

  validateCoupon(code: string, amount: number) {
    return this.apiService.post('orders/validate-coupon', { code, amount });
  }

  getActiveCoupons() {
    return this.apiService.get('orders/coupons');
  }
}


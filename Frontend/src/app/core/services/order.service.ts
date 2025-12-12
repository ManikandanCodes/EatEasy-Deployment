import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { OrdersComponent } from '../../features/restaurant/orders/orders.component';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  constructor(private api: ApiService) { }


  getCustomerOrders(userId: number): Observable<OrdersComponent[]> {
    return this.api.get<OrdersComponent[]>(`orders/customer/${userId}`);
  }


  getOrderById(orderId: number): Observable<OrdersComponent> {
    return this.api.get<OrdersComponent>(`orders/${orderId}`);
  }


  placeOrder(data: any): Observable<OrdersComponent> {
    return this.api.post<OrdersComponent>('orders', data);
  }



  getRestaurantOrders(): Observable<OrdersComponent[]> {
    return this.api.get<OrdersComponent[]>('orders/restaurant');
  }

  updateOrderStatus(orderId: number, status: string): Observable<OrdersComponent> {
    return this.api.put<OrdersComponent>(`orders/${orderId}/status?status=${status}`, {});
  }

  submitReview(reviewData: any): Observable<any> {
    return this.api.post('reviews', reviewData);
  }

  rateOrder(orderId: number, rating: number, comment: string): Observable<any> {
    return this.api.post(`orders/${orderId}/rate`, { rating, comment });
  }

  validateCoupon(code: string): Observable<any> {
    return this.api.post('orders/validate-coupon', { code });
  }
}

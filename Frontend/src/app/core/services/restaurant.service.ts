import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class RestaurantService {

  constructor(private api: ApiService) { }

  createRestaurant(data: any): Observable<any> {
    return this.api.post('restaurants', data);
  }

  getMyRestaurant(): Observable<any> {
    return this.api.get('restaurant/my');
  }

  getAllRestaurants(): Observable<any> {
    return this.api.get('restaurants');
  }

  getRestaurantById(id: number): Observable<any> {
    return this.api.get(`restaurants/${id}`);
  }

  updateRestaurant(id: number, data: any): Observable<any> {
    return this.api.put(`restaurants/${id}`, data);
  }

  deleteRestaurant(id: number): Observable<any> {
    return this.api.delete(`restaurants/${id}`);
  }

  updateStatus(id: number, open: boolean): Observable<any> {
    return this.api.put(`restaurant/status/${id}?open=${open}`, {});
  }
}

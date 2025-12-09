import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Category {
  id: number;
  name: string;
  restaurantId?: number;
}

export interface MenuItem {
  id: number;
  name: string;
  description?: string;
  price: number;
  imageUrl?: string;
  categoryId: number;
  available?: boolean;
  editing?: boolean;
  editName?: string;
  editPrice?: number;
  editCategoryId?: number;
  editDescription?: string;
  editImageUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class MenuService {

  constructor(private api: ApiService) { }


  getCategories(restaurantId?: number): Observable<Category[]> {
    if (restaurantId) {
      return this.api.get<Category[]>(`categories/restaurant/${restaurantId}`);
    }

    return this.api.get<Category[]>('categories');
  }

  addCategory(cat: Partial<Category>): Observable<Category> {
    return this.api.post<Category>('categories', cat);
  }

  updateCategory(id: number, cat: Partial<Category>) {
    return this.api.put(`categories/${id}`, cat);
  }

  deleteCategory(id: number) {
    return this.api.delete(`categories/${id}`);
  }


  getItems(): Observable<MenuItem[]> {
    return this.api.get<MenuItem[]>('menu-items');
  }

  getItemsByCategory(categoryId: number): Observable<MenuItem[]> {
    return this.api.get<MenuItem[]>(`menu-items/category/${categoryId}`);
  }

  getItemsByRestaurant(restaurantId: number): Observable<MenuItem[]> {
    return this.api.get<MenuItem[]>(`menu-items/restaurant/${restaurantId}`);
  }

  addItem(item: Partial<MenuItem>) {
    return this.api.post<MenuItem>('menu-items', item);
  }

  updateItem(id: number, item: Partial<MenuItem>) {
    return this.api.put(`menu-items/${id}`, item);
  }

  deleteItem(id: number) {
    return this.api.delete(`menu-items/${id}`);
  }
}

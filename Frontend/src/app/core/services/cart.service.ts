import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private cart: any = {};
  private cartSubject = new BehaviorSubject<any>(this.cart);
  cart$ = this.cartSubject.asObservable();

  constructor(private authService: AuthService) {
    this.authService.loginStatus().subscribe(() => {
      this.loadCart();
    });
  }

  private getCartKey(): string {
    const user = this.authService.getUser();
    return user ? `cart_${user.id}` : 'cart_guest';
  }

  private saveCart() {
    localStorage.setItem(this.getCartKey(), JSON.stringify(this.cart));
    this.cartSubject.next(this.cart);
  }

  private loadCart() {
    const savedCart = localStorage.getItem(this.getCartKey());
    if (savedCart) {
      try {
        const parsedCart = JSON.parse(savedCart);

        this.cart = {};
        Object.keys(parsedCart).forEach(key => {
          const item = parsedCart[key];
          if (item && item.id && item.name && item.price != null && item.quantity) {
            this.cart[item.id] = item;
          }
        });

        this.cartSubject.next(this.cart);
      } catch (e) {
        console.error('Error parsing cart from local storage', e);
        this.cart = {};
        this.cartSubject.next(this.cart);
      }
    } else {
      this.cart = {};
      this.cartSubject.next(this.cart);
    }
  }


  addItem(item: any) {
    if (!item.id) {
      console.error('Cannot add item without ID to cart', item);
      return;
    }
    if (this.cart[item.id]) {
      this.cart[item.id].quantity++;
    } else {
      this.cart[item.id] = { ...item, quantity: 1 };
    }
    this.saveCart();
  }

  increaseQuantity(id: number) {
    if (this.cart[id]) {
      this.cart[id].quantity++;
      this.saveCart();
    }
  }

  decreaseQuantity(id: number) {
    if (this.cart[id]) {
      this.cart[id].quantity--;
      if (this.cart[id].quantity === 0) {
        delete this.cart[id];
      }
      this.saveCart();
    }
  }

  getCartQuantities() {
    const q: any = {};
    for (const id in this.cart) {
      q[id] = this.cart[id].quantity;
    }
    return q;
  }

  getCartItems() {
    return Object.values(this.cart);
  }

  removeItem(id: number) {
    delete this.cart[id];
    this.saveCart();
  }


  clearCart() {
    this.cart = {};
    this.saveCart();
  }

}

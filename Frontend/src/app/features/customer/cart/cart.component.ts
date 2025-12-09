import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {

  items: any[] = [];
  subtotal = 0;
  deliveryFee = 20;
  tax = 0;
  total = 0;

  constructor(private cartService: CartService, private router: Router) { }

  ngOnInit() {
    this.cartService.cart$.subscribe(cart => {
      this.items = Object.values(cart);
      this.calculateTotals();
    });
  }

  loadCart() {

    this.items = this.cartService.getCartItems();
    this.calculateTotals();
  }

  increase(item: any) {
    this.cartService.increaseQuantity(item.id);
    this.loadCart();
  }

  decrease(item: any) {
    this.cartService.decreaseQuantity(item.id);
    this.loadCart();
  }

  remove(item: any) {
    this.cartService.removeItem(item.id);
    this.loadCart();
  }

  calculateTotals() {
    this.subtotal = this.items.reduce((sum, item) => {
      const price = Number(item.price) || 0;
      const quantity = Number(item.quantity) || 0;
      return sum + (price * quantity);
    }, 0);
    this.tax = Math.round(this.subtotal * 0.05); 
    this.total = this.subtotal + this.tax + this.deliveryFee;
  }

  proceedToCheckout() {
    this.router.navigate(['/customer/checkout']);
  }
}

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../../core/services/cart.service';
import { CustomerService } from '../../../core/services/customer.service';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent {

  items: any[] = [];
  subtotal = 0;
  deliveryFee = 20;
  tax = 0;
  total = 0;


  name = '';
  phone = '';
  address = '';
  instructions = '';
  paymentMethod = 'COD';


  couponCode = '';
  discountAmount = 0;
  appliedCoupon: any = null;

  constructor(
    private cartService: CartService,
    private customerService: CustomerService,
    private authService: AuthService,
    private router: Router
  ) {
    this.loadCart();
  }

  loadCart() {
    this.items = this.cartService.getCartItems();
    this.subtotal = this.items.reduce((t, i) => t + i.price * i.quantity, 0);
    this.tax = Math.round(this.subtotal * 0.05);
    this.calculateTotal();
  }

  calculateTotal() {
    this.total = this.subtotal + this.tax + this.deliveryFee - this.discountAmount;
    if (this.total < 0) this.total = 0;
  }

  applyCoupon() {
    if (!this.couponCode.trim()) {
      alert('Please enter a coupon code');
      return;
    }

    this.customerService.validateCoupon(this.couponCode, this.subtotal).subscribe({
      next: (coupon: any) => {
        this.appliedCoupon = coupon;

        if (coupon.type === 'PERCENTAGE') {
          this.discountAmount = Math.round((this.subtotal * coupon.discount) / 100);
        } else {
          this.discountAmount = coupon.discount;
        }


        if (this.discountAmount > this.subtotal) {
          this.discountAmount = this.subtotal;
        }

        this.calculateTotal();
        alert(`Coupon applied! You saved ₹${this.discountAmount}`);
      },
      error: (err) => {
        console.error('Coupon error', err);
        alert('Invalid or expired coupon code');
        this.appliedCoupon = null;
        this.discountAmount = 0;
        this.calculateTotal();
      }
    });
  }

  placeOrder() {
    if (!this.address || !this.phone) {
      alert("Please enter delivery address & phone.");
      return;
    }

    const user = this.authService.getUser();
    if (!user) {
      alert("Please login to place an order");
      this.router.navigate(['/login']);
      return;
    }

    if (this.items.length === 0) {
      alert("Cart is empty");
      return;
    }

    const orderRequest = {
      userId: user.id,
      restaurantId: this.items[0].restaurantId,
      items: this.items.map(item => ({
        menuItemId: item.id,
        quantity: item.quantity,
        price: item.price
      })),
      deliveryAddress: this.address,
      phone: this.phone,
      totalAmount: this.total
    };

    this.customerService.placeOrder(orderRequest).subscribe({
      next: (res: any) => {
        alert("Order placed successfully!");
        this.cartService.clearCart();
        this.router.navigate(['/customer/track', res.id]);
      },
      error: (err) => {
        console.error("Order placement failed", err);
        alert("Failed to place order. Please try again.");
      }
    });
  }
}

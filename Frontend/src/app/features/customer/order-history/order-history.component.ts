import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../../../core/services/customer.service';
import { OrderService } from '../../../core/services/order.service';
import { Router, RouterModule } from '@angular/router';

import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css']
})
export class OrderHistoryComponent implements OnInit {

  orders: any[] = [];
  loading = true;


  showRatingModal = false;
  selectedOrder: any = null;
  rating = 0;
  comment = '';

  constructor(
    private customerService: CustomerService,
    private orderService: OrderService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.customerService.getOrderHistory().subscribe({
      next: (res: any) => {
        this.orders = Array.isArray(res) ? res : [];
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  reorder(order: any) {
    const restaurantId = order.restaurant?.id || order.restaurantId;
    if (restaurantId) {
      this.router.navigate(['/customer/restaurant', restaurantId]);
    } else {
      console.error('Restaurant ID not found for order:', order);
    }
  }

  trackOrder(orderId: number) {
    this.router.navigate(['/customer/track', orderId]);
  }


  rateOrder(order: any) {
    this.selectedOrder = order;
    this.rating = 0;
    this.comment = '';
    this.showRatingModal = true;
  }


  setRating(star: number) {
    this.rating = star;
  }


  closeRatingModal() {
    this.showRatingModal = false;
    this.selectedOrder = null;
  }


  submitRating() {
    if (this.rating === 0) {
      alert("Please select a star rating.");
      return;
    }

    const reviewData = {
      orderId: this.selectedOrder.id,
      rating: this.rating,
      comment: this.comment
    };

    this.orderService.submitReview(reviewData).subscribe({
      next: () => {
        alert("Thank you for your review!");
        this.closeRatingModal();
      },
      error: (err) => {
        console.error("Error submitting review:", err);
        alert("Failed to submit review. You may have already reviewed this order.");
        this.closeRatingModal();
      }
    });
  }
}

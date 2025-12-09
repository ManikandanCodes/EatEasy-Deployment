import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService } from '../../../core/services/order.service';

@Component({
  selector: 'app-orders',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {

  orders: any[] = [];
  loading = true;

  constructor(private orderService: OrderService) { }

  ngOnInit() {
    this.fetchOrders();
  }

  fetchOrders() {
    this.orderService.getRestaurantOrders().subscribe({
      next: (res) => {
        console.log('Orders fetched:', res);
        this.orders = res;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching orders:', err);
        this.loading = false;
      }
    });
  }

  updateStatus(orderId: number, status: string) {
    this.orderService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        console.log(`Order ${orderId} status updated to ${status}`);
        this.fetchOrders();
      },
      error: (err) => {
        console.error(`Error updating order ${orderId} status:`, err);
      }
    });
  }
}
